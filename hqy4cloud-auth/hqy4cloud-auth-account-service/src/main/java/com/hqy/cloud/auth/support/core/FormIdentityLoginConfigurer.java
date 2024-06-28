package com.hqy.cloud.auth.support.core;

import com.hqy.cloud.auth.support.handler.FormAuthenticationFailureHandler;
import com.hqy.cloud.auth.support.handler.SsoLogoutSuccessHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * 基于授权码模式 统一认证登录 spring security & sas 都可以使用 所以抽取成 HttpConfigurer
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24 16:13
 */
public class FormIdentityLoginConfigurer extends AbstractHttpConfigurer<FormIdentityLoginConfigurer, HttpSecurity> {

    @Override
    public void init(HttpSecurity http) throws Exception {
        http.formLogin(formLogin -> {
                    formLogin.loginPage("/auth/token");
                    formLogin.loginProcessingUrl("/token/form");
                    formLogin.failureHandler(new FormAuthenticationFailureHandler());

                }).logout((logout) -> {
                    logout.deleteCookies("JSESSIONID")
                            .invalidateHttpSession(true)
                            .logoutUrl("/auth/logout")
                            .logoutSuccessHandler(new SsoLogoutSuccessHandler());
                }).csrf(AbstractHttpConfigurer::disable);
    }

}
