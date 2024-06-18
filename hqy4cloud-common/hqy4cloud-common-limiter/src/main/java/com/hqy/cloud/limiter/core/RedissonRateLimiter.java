package com.hqy.cloud.limiter.core;

import com.hqy.cloud.limiter.api.AbstractLimiter;
import com.hqy.cloud.limiter.flow.FlowLimitConfig;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

/**
 * 基于redisson 的 RRateLimiter 进行限流
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/3
 */
public class RedissonRateLimiter extends AbstractLimiter {
    private final RedissonClient redissonClient;
    public RedissonRateLimiter(FlowLimitConfig config, RedissonClient redissonClient) {
        super(config);
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean isOverLimit(String resource) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(resource);
        if (!rateLimiter.isExists()) {
            rateLimiter.setRate(RateType.OVERALL, getConfig().getCount(), getConfig().getWindows(), RateIntervalUnit.SECONDS);
        }
        return rateLimiter.tryAcquire();
    }
}
