package com.hqy.cloud.auth.autoconfigure;

import com.hqy.cloud.auth.api.AuthPermissionService;
import com.hqy.cloud.auth.api.support.AuthenticationAspect;
import com.hqy.cloud.auth.api.support.DefaultAuthPermissionService;
import com.hqy.cloud.limiter.api.ManualWhiteIpService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/20
 */
@Configuration
public class AuthAutoConfiguration {

    @Bean
    public AuthPermissionService authPermissionService(Environment environment, ManualWhiteIpService manualWhiteIpService) {
        return new DefaultAuthPermissionService(environment, manualWhiteIpService);
    }

    @Bean
    public AuthenticationAspect authenticationAspect(AuthPermissionService authPermissionService) {
        return new AuthenticationAspect(authPermissionService);
    }


}
