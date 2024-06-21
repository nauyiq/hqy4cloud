package com.hqy.cloud.lock.autoconfigure;

import com.hqy.cloud.lock.server.DistributeLockAspect;
import com.hqy.cloud.lock.service.LockService;
import com.hqy.cloud.lock.service.impl.RedissonLockServiceImpl;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/19
 */
@Configuration
public class DistributeLockAutoConfiguration {

    @Bean
    @ConditionalOnBean
    @ConditionalOnMissingBean
    public LockService lockService(RedissonClient redissonClient) {
        return new RedissonLockServiceImpl(redissonClient);
    }

    @Bean
    public DistributeLockAspect distributeLockAspect(LockService lockService) {
        return new DistributeLockAspect(lockService);
    }

}
