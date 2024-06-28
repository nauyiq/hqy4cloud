package com.hqy.cloud.auth.config;

import com.hqy.cloud.auth.support.core.DefaultDaoAuthenticationProvider;
import com.hqy.cloud.auth.support.core.FormIdentityLoginConfigurer;
import com.hqy.cloud.auth.utils.StaticEndpointAuthorizationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
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
        http.authorizeHttpRequests(authorizeRequests ->
                // 开放options请求
                authorizeRequests.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 开放静态白名单端点
                .requestMatchers(StaticEndpointAuthorizationManager.getInstance().getWhiteEndpointsPatterns().toArray(new String[0])).permitAll()
                .anyRequest().authenticated())
            // 避免iframe同源无法登录
            .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            // druid监控页面允许iframe
            .securityMatcher("/druid").headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .with(new FormIdentityLoginConfigurer(), c -> {})
            .authenticationProvider(new DefaultDaoAuthenticationProvider(securityMessageSource));


        /*http.authorizeRequests(authorizeRequests -> authorizeRequests
                // 开放白名单端点
                .antMatchers(EndpointAuthorizationManager.ENDPOINTS.toArray(new String[0])).permitAll()

                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                .anyRequest().authenticated()).headers().frameOptions().sameOrigin()

                .and().antMatcher("/druid").headers().frameOptions().disable()
                // 表单登录个性化
                .and().apply(new FormIdentityLoginConfigurer());*/
                // 处理 UsernamePasswordAuthenticationToken
        http.authenticationProvider(new DefaultDaoAuthenticationProvider(securityMessageSource));
        return http.build();
    }



}
