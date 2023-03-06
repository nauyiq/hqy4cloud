package com.hqy.cloud.gateway.config;

import com.hqy.cloud.auth.limit.support.BiBlockedIpRedisService;
import com.hqy.cloud.auth.limit.support.ManualBlockedIpService;
import com.hqy.cloud.auth.limit.support.ManualWhiteIpRedisService;
import com.hqy.cloud.auth.server.Oauth2Access;
import com.hqy.cloud.auth.server.RolesAuthoritiesChecker;
import com.hqy.cloud.auth.server.UploadFileSecurityChecker;
import com.hqy.cloud.auth.server.support.DefaultUploadFileSecurityChecker;
import com.hqy.cloud.auth.server.support.NacosOauth2Access;
import com.hqy.cloud.auth.server.support.ResourceInRoleCacheServer;
import com.hqy.cloud.gateway.server.auth.AuthorizationManager;
import com.hqy.cloud.gateway.server.auth.GatewayReactOauth2AuthoritiesChecker;
import com.hqy.foundation.limit.service.BlockedIpService;
import com.hqy.foundation.limit.service.ManualWhiteIpService;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/6 11:16
 */
@Configuration
public class GatewayAutoConfiguration {

    @Bean
    public ResourceInRoleCacheServer resourceInRoleCacheServer(RedissonClient redissonClient) {
        return new ResourceInRoleCacheServer(redissonClient);
    }

    @Bean
    public Oauth2Access oauth2Access(ManualWhiteIpService manualWhiteIpService) {
        return new NacosOauth2Access(manualWhiteIpService);
    }

    @Bean
    public AuthorizationManager authorizationManager(RolesAuthoritiesChecker gatewayReactOauth2AuthoritiesChecker, Oauth2Access oauth2Access) {
        return new AuthorizationManager(gatewayReactOauth2AuthoritiesChecker, oauth2Access);
    }

    @Bean
    public ManualWhiteIpService manualWhiteIpService(RedissonClient redisson) {
        return new ManualWhiteIpRedisService(redisson);
    }

    @Bean
    public BlockedIpService biBlockedIpService() {
        return new BiBlockedIpRedisService(true);
    }

    @Bean
    public BlockedIpService manualBlockedIpService() {
        return new ManualBlockedIpService(true);
    }


    @Bean
    public GatewayReactOauth2AuthoritiesChecker gatewayReactOauth2AuthoritiesChecker(ResourceInRoleCacheServer resourceInRoleCacheServer) {
        return new GatewayReactOauth2AuthoritiesChecker(resourceInRoleCacheServer);
    }

    @Bean
    public UploadFileSecurityChecker uploadFileSecurityChecker() {
        return new DefaultUploadFileSecurityChecker();
    }
}
