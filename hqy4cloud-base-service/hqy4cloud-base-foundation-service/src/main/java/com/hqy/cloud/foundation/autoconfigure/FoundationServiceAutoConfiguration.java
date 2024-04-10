package com.hqy.cloud.foundation.autoconfigure;

import com.hqy.cloud.foundation.authorization.JwtAuthorizationService;
import com.hqy.cloud.foundation.domain.DomainServer;
import com.hqy.cloud.foundation.domain.support.DefaultDomainServer;
import com.hqy.cloud.foundation.router.HashRouterServiceImpl;
import com.hqy.foundation.authorization.AuthorizationService;
import com.hqy.foundation.router.HashRouterService;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

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

    @Bean
    @ConditionalOnMissingBean
    public DomainServer domainServer(Environment environment) {
        return new DefaultDomainServer(environment);
    }

}
