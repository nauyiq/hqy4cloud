package com.hqy.cloud.auth.autoconfigure;

import com.hqy.cloud.auth.api.AuthPermissionService;
import com.hqy.cloud.auth.api.AuthoritiesRoleService;
import com.hqy.cloud.auth.api.support.AuthenticationAspect;
import com.hqy.cloud.auth.api.support.RemoteAccountAuthoritiesRoleService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    @ConditionalOnBean
    public AuthenticationAspect authenticationAspect(AuthPermissionService authPermissionService) {
        return new AuthenticationAspect(authPermissionService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthoritiesRoleService authoritiesRoleService() {
        return new RemoteAccountAuthoritiesRoleService();
    }

}
