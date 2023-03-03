package com.hqy.cloud.auth.core.support;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Collection;
import java.util.Map;

/**
 * credential 支持客户端模式的用户存储
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/2 15:43
 */
@RequiredArgsConstructor
public class CredentialsOAuth2AuthenticatedPrincipal implements OAuth2AuthenticatedPrincipal {
    private final String name;
    private final Map<String, Object> attributes;
    private final Collection<GrantedAuthority> authorities;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return name;
    }
}
