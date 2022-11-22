package com.hqy.security.server.email;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 16:25
 */
public class EmailCodeAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private String code;

    public EmailCodeAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Object principal, String code) {
        super(authorities);
        this.principal = principal;
        this.code = code;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
