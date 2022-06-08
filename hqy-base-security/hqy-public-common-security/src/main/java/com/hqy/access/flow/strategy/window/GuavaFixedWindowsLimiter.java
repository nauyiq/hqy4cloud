package com.hqy.access.flow.strategy.window;

import com.alibaba.csp.sentinel.util.TimeUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 单机版基于guava缓存的固定窗口限流器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/7 10:50
 */
@Slf4j
public class GuavaFixedWindowsLimiter extends AbstractWindowLimiter {

    private final LoadingCache<Long, AtomicInteger> counter;

    public GuavaFixedWindowsLimiter(long millisecondWindow, int threshold) {
        super(millisecondWindow, threshold);
        counter = CacheBuilder.newBuilder().expireAfterWrite( getSecondWindows() + 1, TimeUnit.SECONDS)
                .build(new CacheLoader<Long, AtomicInteger>() {
                    @Override
                    public AtomicInteger load(Long timestamp) throws Exception {
                        return new AtomicInteger(0);
                    }
                });
    }


    @Override
    protected long currentCount() {
        long currentSecond = TimeUtil.currentTimeMillis() / 1000;
        try {
            return counter.get(currentSecond).getAndIncrement();
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        GuavaFixedWindowsLimiter limiter = new GuavaFixedWindowsLimiter(1L, 5);
        for (int i = 0; i < 100; i++) {
            boolean limit = limiter.isLimit();
            if (limit) {
                TimeUnit.MILLISECONDS.sleep(100L);
            }
            System.out.println("current thread name limit " + limit);
        }
    }


}
