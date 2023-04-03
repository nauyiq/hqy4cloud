package com.hqy.cloud.gateway.server.auth;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

/**
 * basic认证
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/13 18:10
 */
public class ClientSecretReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication);
    }
}
