package com.hqy.cloud.lock.config;

import com.hqy.cloud.cache.config.IRedisAutoConfiguration;
import com.hqy.cloud.lock.server.DistributeLockAspect;
import com.hqy.cloud.lock.service.LockService;
import com.hqy.cloud.lock.service.impl.RedissonLockServiceImpl;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/19
 */
@Configuration
@AutoConfigureAfter(IRedisAutoConfiguration.class)
public class DistributeLockAutoConfiguration {

    @Bean
    public LockService lockService(RedissonClient redissonClient) {
        return new RedissonLockServiceImpl(redissonClient);
    }

    @Bean
    public DistributeLockAspect distributeLockAspect(LockService lockService) {
        return new DistributeLockAspect(lockService);
    }

}
