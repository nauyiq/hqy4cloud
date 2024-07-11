package com.hqy.cloud.auth.autoconfigure;

import com.hqy.cloud.auth.api.AuthPermissionService;
import com.hqy.cloud.auth.api.support.AuthenticationAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/20
 */
@Configuration
public class BaseAuthAutoConfiguration {

    @Bean
    public AuthenticationAspect authenticationAspect(AuthPermissionService authPermissionService) {
        return new AuthenticationAspect(authPermissionService);
    }


}
