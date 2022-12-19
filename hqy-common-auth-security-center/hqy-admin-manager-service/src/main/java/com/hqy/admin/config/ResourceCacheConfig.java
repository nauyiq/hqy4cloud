package com.hqy.admin.config;

import com.hqy.access.auth.support.ResourceInRoleCacheServer;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/19 13:57
 */
@Configuration
public class ResourceCacheConfig {

    @Bean
    public ResourceInRoleCacheServer resourceInRoleCacheServer(RedissonClient redissonClient) {
        return new ResourceInRoleCacheServer(redissonClient);
    }

}
