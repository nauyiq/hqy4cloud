package com.hqy.cloud.auth.autoconfigure;

import com.hqy.cloud.auth.api.AuthPermissionService;
import com.hqy.cloud.auth.api.support.DefaultAuthPermissionService;
import com.hqy.cloud.auth.core.AuthorizationResourceRepository;
import com.hqy.cloud.limiter.api.ManualWhiteIpService;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/20
 */
@Configuration
public class AuthAutoConfiguration {

    @Bean
    public AuthPermissionService authPermissionService(AuthorizationResourceRepository authorizationResourceRepository, ManualWhiteIpService manualWhiteIpService) {
        return new DefaultAuthPermissionService(authorizationResourceRepository, manualWhiteIpService);
    }
    @Bean
    public AuthorizationResourceRepository authorizationResourceRepository(RedissonClient redissonClient) {
        return new AuthorizationResourceRepository(redissonClient);
    }


}
