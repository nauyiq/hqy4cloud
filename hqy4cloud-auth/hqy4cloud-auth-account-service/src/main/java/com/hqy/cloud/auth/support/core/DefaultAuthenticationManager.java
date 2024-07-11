package com.hqy.cloud.auth.support.core;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/10
 */
public class DefaultAuthenticationManager implements AuthenticationManager {


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Object principal = authentication.getPrincipal();


        return null;
    }
}
