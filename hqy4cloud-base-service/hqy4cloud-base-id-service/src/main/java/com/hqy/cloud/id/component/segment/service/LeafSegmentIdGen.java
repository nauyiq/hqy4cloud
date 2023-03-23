package com.hqy.cloud.id.component.segment.service;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.id.component.segment.model.Segment;
import com.hqy.cloud.id.component.segment.model.SegmentBuffer;
import com.hqy.cloud.id.entities.LeafAlloc;
import com.hqy.cloud.id.service.IdGen;
import com.hqy.cloud.id.service.LeafAllocTkService;
import com.hqy.cloud.id.struct.ResultStruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/21 15:25
 */
@Slf4j
@Component
@RefreshScope
@RequiredArgsConstructor
public class LeafSegmentIdGen implements IdGen {

    private final LeafAllocTkService leafAllocTkService;

    /**
     * 最大步长 默认为100,0000
     */
    @Value("${id.segment.maxStep:1000000}")
    private int maxStep;

    /**
     * 一个Segment维持时间， 默认为15分钟
     */
    @Value("${id.segment.duration:900000}")
    private long segmentDuration;

    /**
     * 加载因子
     */
    @Value("${id.segment.loadFactor:2}")
    private int loadFactor;

    /**
     * 刷新segment buffer线程池
     */
    private final ExecutorService service = new ThreadPoolExecutor(Math.min(Runtime.getRuntime().availableProcessors(), 5),
            Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new UpdateThreadFactory());

    private volatile boolean initOk = false;
    private static final SecureRandom RANDOM = new SecureRandom();
    private final Map<String, SegmentBuffer> cache = MapUtil.newConcurrentHashMap();

    /**
     * IDCache未初始化成功时的异常码
     */
    private static final long EXCEPTION_ID_CACHE_INIT_FALSE = -1;

    /**
     * key不存在时的异常码
     */
    private static final long EXCEPTION_ID_KEY_NOT_EXISTS = -2;

    /**
     * SegmentBuffer中的两个Segment均未从DB中装载时的异常码
     */
    private static final long EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL = -3;


    @Override
    public boolean init() {
        log.info("Start init LeafSegmentIdGen...");
        // 确保加载到kv后才初始化成功
        updateCacheFromDb();
        initOk = true;
        updateCacheFromDbAtEveryMinute();
        return initOk;
    }

    @Override
    public ResultStruct get(String key) {
        if (!initOk) {
            return new ResultStruct(EXCEPTION_ID_CACHE_INIT_FALSE, false);
        }
        SegmentBuffer buffer = cache.get(key);
        if (Objects.nonNull(buffer)) {
            if (!buffer.isInitOk()) {
                synchronized (buffer){
                    if (!buffer.isInitOk()) {
                        try {
                            updateSegmentFromDb(key, buffer.getCurrent());
                            log.info("Init buffer. Update leaf key {} {} from db", key, buffer.getCurrent());
                            buffer.setInitOk(true);
                        } catch (Exception e) {
                            log.warn("Init Segment buffer {} exception.", buffer.getCurrent(), e);
                        }
                    }
                }
            }
            return getIdFromSegmentBuffer(cache.get(key));
        }

        return new ResultStruct(EXCEPTION_ID_KEY_NOT_EXISTS, false);
    }



    private void updateCacheFromDb() {
        log.info("Start updateCacheFromDb by LeafSegmentIdGen.");
        try {
            //加载所有的业务标志bizTag, 即主键.
            List<String> dbTags = leafAllocTkService.getAllTags();
            if (CollectionUtils.isEmpty(dbTags)) {
                return;
            }
            List<String> cacheTags = new ArrayList<>(cache.keySet());
            Set<String> insertTagsSet = new HashSet<>(dbTags);
            Set<String> removeTagsSet = new HashSet<>(cacheTags);

            // db中新加的tags灌进cache
            for (String cacheTag : cacheTags) {
                insertTagsSet.remove(cacheTag);
            }
            for (String tag : insertTagsSet) {
                SegmentBuffer buffer = new SegmentBuffer();
                buffer.setKey(tag);
                Segment segment = buffer.getCurrent();
                segment.setValue(new AtomicLong(0));
                segment.setMax(0);
                segment.setStep(0);
                cache.put(tag, buffer);
                log.info("Add tag {} from db to IdCache, SegmentBuffer {}", tag, buffer);
            }

            // cache中已失效的tags从cache删除
            for (String dbTag : dbTags) {
                removeTagsSet.remove(dbTag);
            }
            for (String tag : removeTagsSet) {
                cache.remove(tag);
                log.info("Remove tag {} from IdCache", tag);
            }

        } catch (Throwable cause) {
            log.warn("Failed execute to updateCacheFromDb by LeafSegmentIdGen.", cause);
        }
    }

    private void updateCacheFromDbAtEveryMinute() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setName("check-idCache-thread");
            t.setDaemon(true);
            return t;
        });
        service.scheduleWithFixedDelay(this::updateCacheFromDb, 60, 60, TimeUnit.SECONDS);
    }

    private void updateSegmentFromDb(String key, Segment segment) {
        SegmentBuffer buffer = segment.getBuffer();
        LeafAlloc leafAlloc;
        if (!buffer.isInitOk()) {
            leafAlloc = leafAllocTkService.updateMaxIdAndGetLeafAlloc(key);
            buffer.setStep(leafAlloc.getStep());
            // leafAlloc中的step为DB中的step
            buffer.setMinStep(leafAlloc.getStep());
        } else if (buffer.getUpdateTimestamp() == 0) {
            leafAlloc = leafAllocTkService.updateMaxIdAndGetLeafAlloc(key);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(leafAlloc.getStep());
            // leafAlloc中的step为DB中的step
            buffer.setMinStep(leafAlloc.getStep());
        } else {
            long duration = System.currentTimeMillis() - buffer.getUpdateTimestamp();
            int nextStep = buffer.getStep();
            if (duration < this.segmentDuration) {
                if (nextStep * this.loadFactor <= this.maxStep) {
                    nextStep = nextStep * this.loadFactor;
                }
            } else if (duration < this.segmentDuration * this.loadFactor) {
                // do nothing with nextStep
            } else {
                nextStep = nextStep / this.loadFactor >= buffer.getMinStep() ? nextStep / this.loadFactor : nextStep;
            }
            log.info("leafKey[{}], step[{}], duration[{} minutes], nextStep[{}]", key, buffer.getStep(),
                    String.format("%.2f", ((double) duration / (1000 * 60))), nextStep);
            LeafAlloc temp = new LeafAlloc();
            temp.setKey(key);
            temp.setStep(nextStep);
            leafAlloc = leafAllocTkService.updateMaxIdByCustomStepAndGetLeafAlloc(temp);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(nextStep);
            // leafAlloc的step为DB中的step
            buffer.setMinStep(leafAlloc.getStep());
        }
        // must set value before set max
        long value = leafAlloc.getMaxId() - buffer.getStep();
        segment.getValue().set(value);
        segment.setMax(leafAlloc.getMaxId());
        segment.setStep(buffer.getStep());
        segment.setRandomStep(leafAlloc.getRandomStep());
    }


    private ResultStruct getIdFromSegmentBuffer(SegmentBuffer buffer) {
        while (true) {
            buffer.rLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                if (!buffer.isNextReady() && (segment.getIdle() < 0.9 * segment.getStep())
                        && buffer.getThreadRunning().compareAndSet(false, true)) {
                    service.execute(() -> {
                        Segment next = buffer.getSegments()[buffer.nextPos()];
                        boolean updateOk = false;
                        try {
                            updateSegmentFromDb(buffer.getKey(), next);
                            updateOk = true;
                            log.info("update segment {} from db {}", buffer.getKey(), next);
                        }
                        catch (Exception e) {
                            log.warn(buffer.getKey() + " updateSegmentFromDb exception", e);
                        } finally {
                            if (updateOk) {
                                buffer.wLock().lock();
                                buffer.setNextReady(true);
                                buffer.getThreadRunning().set(false);
                                buffer.wLock().unlock();
                            }
                            else {
                                buffer.getThreadRunning().set(false);
                            }
                        }
                    });

                }
                long value;
                if (segment.getRandomStep() > 1) {
                    // 随机从1-10里面增加
                    value = segment.getValue().getAndAdd(randomAdd(segment.getRandomStep()));
                }
                else {
                    value = segment.getValue().getAndIncrement();
                }

                if (value < segment.getMax()) {
                    return new ResultStruct(value, true);
                }

            } finally {
                buffer.rLock().unlock();
            }
            waitAndSleep(buffer);
            buffer.wLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                long value = segment.getValue().getAndIncrement();
                if (value < segment.getMax()) {
                    return new ResultStruct(value, true);
                }
                if (buffer.isNextReady()) {
                    buffer.switchPos();
                    buffer.setNextReady(false);
                }
                else {
                    log.error("Both two segments in {} are not ready!", buffer);
                    return new ResultStruct(EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL, false);
                }
            }
            finally {
                buffer.wLock().unlock();
            }
        }

    }

    private void waitAndSleep(SegmentBuffer buffer) {
        int roll = 0;
        while (buffer.getThreadRunning().get()) {
            roll += 1;
            if (roll > 10000) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                    break;
                }
                catch (InterruptedException e) {
                    log.warn("Thread {} Interrupted, Exception: {}", Thread.currentThread().getName(), e);
                    break;
                }
            }
        }

    }

    private long randomAdd(int randomStep) {
        return RANDOM.nextInt(randomStep - 1) + 1;
    }


    public static class UpdateThreadFactory implements ThreadFactory {
        private static int threadInitNumber = 0;
        private static synchronized int nextThreadNum() {
            return threadInitNumber++;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread-Segment-Update-" + nextThreadNum());
        }

    }
}
