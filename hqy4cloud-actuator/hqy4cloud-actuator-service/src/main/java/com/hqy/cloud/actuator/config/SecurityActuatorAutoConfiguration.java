package com.hqy.cloud.actuator.config;

import cn.hutool.core.lang.UUID;
import de.codecentric.boot.admin.server.config.AdminServerProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/22 11:15
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityActuatorAutoConfiguration  {
    private final AdminServerProperties adminServerProperties;

    @Bean
    @SneakyThrows
    public SecurityFilterChain adminActuatorSecurityFilterChain(HttpSecurity httpSecurity) {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        String contextPath = adminServerProperties.getContextPath();
        httpSecurity.authorizeHttpRequests(c -> {
            c.requestMatchers(contextPath + "/assets/**", contextPath + "/actuator/**", contextPath + "/login").permitAll();
            c.anyRequest().authenticated();
        }).rememberMe((rememberMe) -> rememberMe.key(UUID.fastUUID().toString(true)).tokenValiditySeconds(3600 * 6));

        httpSecurity.formLogin(c -> c.loginPage(contextPath + "/login").successHandler(successHandler))
                .logout(c -> c.logoutUrl(contextPath + "/logout"))
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }



}
