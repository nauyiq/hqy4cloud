package com.hqy.cloud.auth.config;

import com.hqy.cloud.auth.core.support.RedisOAuth2AuthorizationServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/27 15:44
 */
@Configuration
public class SecurityAutoConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService() {
        return new RedisOAuth2AuthorizationServiceImpl();
    }

}
