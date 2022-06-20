package com.hqy.access.flow.strategy;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import com.hqy.access.flow.FlowLimitConfig;
import com.hqy.access.flow.Measurement;

import java.util.concurrent.TimeUnit;

/**
 * 基于guava的令牌桶限流器
 * Guava的 RateLimiter提供了令牌桶算法实现：平滑突发限流(SmoothBursty)和平滑预热限流(SmoothWarmingUp)实现。
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/20 16:36
 */
@SuppressWarnings("all")
public class GuavaCacheTokenBucketLimiter extends AbstractLimiter {

    private final Cache<String, RateLimiter> RATE_LIMITER;

    public GuavaCacheTokenBucketLimiter(FlowLimitConfig config) {
        super(config);
        RATE_LIMITER = CacheBuilder.newBuilder().initialCapacity(512).expireAfterAccess(10L, TimeUnit.MINUTES).build();
    }


    @Override
    public boolean isOverLimit(String resource) {
        //获取限流器
        RateLimiter limiter = getLimiter(resource);
        return !limiter.tryAcquire();
    }


    private RateLimiter getLimiter(String resource) {
        RateLimiter limiter = RATE_LIMITER.getIfPresent(resource);
        if (limiter == null) {
            FlowLimitConfig config = super.getConfig();
            if (config.getWindows() != Measurement.Seconds.ONE_SECONDS) {
                int count = config.getCount() / config.getWindows().seconds;
                limiter = RateLimiter.create(count);
            } else {
                limiter = RateLimiter.create(config.getCount());
            }
            RATE_LIMITER.put(resource, limiter);
        }
        return limiter;
    }
}
