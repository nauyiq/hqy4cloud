package com.hqy.cloud.gateway.config;

import com.hqy.cloud.auth.api.AuthPermissionService;
import com.hqy.cloud.gateway.server.auth.AuthorizationManager;
import com.hqy.cloud.gateway.server.support.GlobalExceptionHandler;
import com.hqy.cloud.limit.core.BiBlockedIpRedisService;
import com.hqy.cloud.limit.core.ManualBlockedIpService;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.socket.client.TomcatWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.upgrade.TomcatRequestUpgradeStrategy;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/6 11:16
 */
@Configuration
public class GatewayConfiguration {

    /*@Bean
    public AuthorizationManager authorizationManager(AuthPermissionService authPermissionService) {
        return new AuthorizationManager(authPermissionService);
    }*/

    @Bean
    public BiBlockedIpRedisService biBlockedIpService(RedissonClient redissonClient) {
        return new BiBlockedIpRedisService(redissonClient);
    }

    @Bean
    public ManualBlockedIpService manualBlockedIpService(RedissonClient redissonClient) {
        return new ManualBlockedIpService(redissonClient);
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    @Primary
    WebSocketClient tomcatWebSocketClient() {
        return new TomcatWebSocketClient();
    }

    @Bean
    @Primary
    public RequestUpgradeStrategy requestUpgradeStrategy() {
        return new TomcatRequestUpgradeStrategy();
    }
}
