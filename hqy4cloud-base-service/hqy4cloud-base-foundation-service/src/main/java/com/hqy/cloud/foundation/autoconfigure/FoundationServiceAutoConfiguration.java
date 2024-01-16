package com.hqy.cloud.foundation.autoconfigure;

import com.hqy.cloud.foundation.authorization.JwtAuthorizationService;
import com.hqy.cloud.foundation.router.HashRouterServiceImpl;
import com.hqy.foundation.authorization.AuthorizationService;
import com.hqy.foundation.router.HashRouterService;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/15
 */
@Configuration(proxyBeanMethods = false)
public class FoundationServiceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public HashRouterService hashRouterService(RedissonClient redissonClient) {
        return new HashRouterServiceImpl(redissonClient);
    }


    @Bean
    @ConditionalOnMissingBean
    public AuthorizationService authorizationService() {
        return new JwtAuthorizationService();
    }

}
