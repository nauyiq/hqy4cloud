package com.hqy.cloud.sharding.autoconfig;

import com.hqy.cloud.sharding.id.WorkerIdHolder;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hongqy
 * @date 2025/6/10
 */
@Configuration
public class WorkerIdHolderAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "workerIdHolder")
    public WorkerIdHolder workerIdHolder(RedissonClient redissonClient) {
        return new WorkerIdHolder(redissonClient);
    }


}
