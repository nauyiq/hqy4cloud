package com.hqy.cloud.auth.security.autoconfigure;

import com.hqy.cloud.auth.api.AuthPermissionService;
import com.hqy.cloud.auth.api.AuthoritiesRoleService;
import com.hqy.cloud.auth.security.core.DefaultAuthPermissionService;
import com.hqy.cloud.auth.api.support.RemoteAccountAuthoritiesRoleService;
import com.hqy.cloud.limit.api.ManualWhiteIpService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/21
 */
@Configuration
public class AuthSpringSecurityAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public AuthPermissionService authPermissionService(Environment environment, AuthoritiesRoleService authoritiesRoleService, ManualWhiteIpService manualWhiteIpService) {
        return new DefaultAuthPermissionService(environment, authoritiesRoleService, manualWhiteIpService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MessageSource securityMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.addBasenames("classpath:i18n/errors/messages");
        messageSource.setDefaultLocale(Locale.CHINA);
        return messageSource;
    }

}
