package com.hqy.cloud.auth.config;

import com.hqy.cloud.auth.core.component.EndpointAuthorizationManager;
import com.hqy.cloud.auth.support.core.DefaultDaoAuthenticationProvider;
import com.hqy.cloud.auth.support.core.FormIdentityLoginConfigurer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 安全拦截机制
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 10:47
 */
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityWebAutoConfiguration {

    /**
     * spring security 默认的安全策略
     * @param http security注入点
     * @return SecurityFilterChain
     * @throws Exception e
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, MessageSource securityMessageSource) throws Exception {
        // 开放自定义的部分端点
        http.authorizeRequests(authorizeRequests -> authorizeRequests
                // 开放白名单端点
                .antMatchers(EndpointAuthorizationManager.ENDPOINTS.toArray(new String[0])).permitAll()
                // 开放options请求
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 避免iframe同源无法登录
                .anyRequest().authenticated()).headers().frameOptions().sameOrigin().
                // 表单登录个性化
                and().apply(new FormIdentityLoginConfigurer());
                // 处理 UsernamePasswordAuthenticationToken
        http.authenticationProvider(new DefaultDaoAuthenticationProvider(securityMessageSource));
        return http.build();
    }



}
