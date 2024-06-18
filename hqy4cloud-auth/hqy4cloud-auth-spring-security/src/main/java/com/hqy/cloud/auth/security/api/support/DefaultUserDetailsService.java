package com.hqy.cloud.auth.security.api.support;

import com.hqy.cloud.auth.security.api.UserDetailsServiceWrapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
public class DefaultUserDetailsService implements UserDetailsServiceWrapper {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
