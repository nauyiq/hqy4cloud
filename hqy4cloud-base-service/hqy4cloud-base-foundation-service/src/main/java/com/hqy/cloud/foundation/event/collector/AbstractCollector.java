package com.hqy.cloud.foundation.event.collector;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.foundation.limiter.FlowLimitConfig;
import com.hqy.cloud.foundation.limiter.GuavaCacheTokenBucketLimiter;
import com.hqy.cloud.foundation.limiter.Limiter;
import com.hqy.cloud.foundation.limiter.Measurement;
import com.hqy.cloud.util.concurrent.AbstractIExecutorService;
import com.hqy.cloud.util.concurrent.IExecutorService;
import com.hqy.cloud.util.concurrent.IExecutorsRepository;
import com.hqy.foundation.event.collection.Collector;
import com.hqy.foundation.event.collection.CollectorConfig;
import com.hqy.foundation.common.EventType;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 数据采集器基类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 17:19
 */
@Slf4j
public abstract class AbstractCollector<T> implements Collector<T> {

    /**
     * 初始化线程池
     */
    private static final String EXECUTOR_NAME = "hqy4cloud-collector";
    static {
        IExecutorsRepository.setExecutor(EXECUTOR_NAME, new AbstractIExecutorService(EXECUTOR_NAME) {});
    }

    /**
     * 采集器配置类
     */
    private CollectorConfig config;

    /**
     * 限流器 防止采集过多
     */
    private Limiter limiter;

    /**
     * 频率计数器， 可能不太准
     * 因为hashcode判断时很大概率发生hash冲突。 但是采集业务允许误差
     */
    private final Map<EventType, AtomicLong> frequencyMap = new ConcurrentHashMap<>();


    @Override
    public void collect(T data) {
        if (data == null) {
            return;
        }

        // 判断是否达到采集频率了,
        int frequency = config.getCollectFrequency();
        boolean isCollect = true;
        AtomicLong dataFrequency = frequencyMap.computeIfAbsent(type(), v -> new AtomicLong(0));
        if (dataFrequency.incrementAndGet() % frequency != 0) {
            isCollect = false;
        }

        // 判断是否采集超限了
        if (isCollect && limiter.isOverLimit(buildLimitResource(data.hashCode()))) {
            // 采集过多了 暂时不采集了...
            log.warn("Collection too many data, type: {}.", type().name());
            isCollect = false;
        }

        // 数据采集
        if (isCollect) {
            try {
                IExecutorService executor = IExecutorsRepository.getExecutor(EXECUTOR_NAME);
                if (config.isCheckQueue() && executor.isQueueNearlyFull()) {
                    // 检查一下队列长度
                    log.warn("Executor: {} is too many task, abandon this collection task.", EXECUTOR_NAME);
                    return;
                }
                executor.execute(() -> doCollect(data));
            } catch (Throwable cause) {
              log.error("Failed execute to do collect, cause: {}.", cause.getMessage(), cause);
            }
        }
    }


    /**
     * 采集数据
     * @param data 被采集数据
     */
    protected abstract void doCollect(T data);


    public AbstractCollector() {
    }

    public AbstractCollector(CollectorConfig config) {
        this.config = config;
        // 创建限流器
        this.limiter = getLimiter(config);
    }

    private GuavaCacheTokenBucketLimiter getLimiter(CollectorConfig config) {
        return new GuavaCacheTokenBucketLimiter(new FlowLimitConfig(config.getRateLimited(),
                Measurement.Seconds.of(config.getRateLimitedWindowSecond(), Measurement.Seconds.ONE_SECONDS)));
    }

    private String buildLimitResource(int dataHashCode) {
        return type().name() + StrUtil.COLON + dataHashCode;
    }

    public CollectorConfig getConfig() {
        return config;
    }

    @Override
    public void setConfig(CollectorConfig config) {
        this.config = config;
        // 更新限流器
        this.limiter = getLimiter(config);
    }
}
