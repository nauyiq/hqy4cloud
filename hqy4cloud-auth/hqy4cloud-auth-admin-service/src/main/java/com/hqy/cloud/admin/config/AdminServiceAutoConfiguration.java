package com.hqy.cloud.admin.config;

import com.hqy.cloud.auth.server.support.ResourceInRoleCacheServer;
import com.hqy.cloud.auth.limit.support.BiBlockedIpRedisService;
import com.hqy.cloud.auth.limit.support.ManualBlockedIpService;
import com.hqy.cloud.auth.limit.support.ManualWhiteIpRedisService;
import com.hqy.foundation.limit.service.ManualWhiteIpService;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/19 13:57
 */
@Configuration
public class AdminServiceAutoConfiguration {

    @Bean
    public ResourceInRoleCacheServer resourceInRoleCacheServer(RedissonClient redissonClient) {
        return new ResourceInRoleCacheServer(redissonClient);
    }

    @Bean
    public ManualWhiteIpService manualWhiteIpService(RedissonClient redissonClient) {
        return new ManualWhiteIpRedisService(redissonClient);
    }

    @Bean
    public BiBlockedIpRedisService biBlockedIpRedisService() {
        return new BiBlockedIpRedisService(false);
    }

    @Bean
    public ManualBlockedIpService manualBlockedIpService() {
        return new ManualBlockedIpService(false);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
