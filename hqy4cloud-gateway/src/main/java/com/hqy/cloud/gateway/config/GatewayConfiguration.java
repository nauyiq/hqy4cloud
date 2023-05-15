package com.hqy.cloud.gateway.config;

import com.hqy.cloud.auth.core.authentication.AuthPermissionService;
import com.hqy.cloud.auth.core.authentication.UploadFileSecurityChecker;
import com.hqy.cloud.auth.core.authentication.support.DefaultUploadFileSecurityChecker;
import com.hqy.cloud.auth.limit.support.BiBlockedIpRedisService;
import com.hqy.cloud.auth.limit.support.ManualBlockedIpService;
import com.hqy.cloud.gateway.server.auth.AuthorizationManager;
import com.hqy.cloud.gateway.server.support.GlobalExceptionHandler;
import com.hqy.foundation.limit.service.BlockedIpService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/6 11:16
 */
@Configuration
public class GatewayConfiguration {

    @Bean
    public AuthorizationManager authorizationManager(AuthPermissionService authPermissionService) {
        return new AuthorizationManager(authPermissionService);
    }

    @Bean
    public BiBlockedIpRedisService biBlockedIpService() {
        return new BiBlockedIpRedisService(true);
    }

    @Bean
    public ManualBlockedIpService manualBlockedIpService() {
        return new ManualBlockedIpService(true);
    }

    @Bean
    public UploadFileSecurityChecker uploadFileSecurityChecker() {
        return new DefaultUploadFileSecurityChecker();
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
