package com.hqy.cloud.auth.security.autoconfigure;

import com.hqy.cloud.auth.api.AuthPermissionService;
import com.hqy.cloud.auth.security.core.DefaultAuthPermissionService;
import com.hqy.cloud.limiter.api.ManualWhiteIpService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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
    public AuthPermissionService authPermissionService(Environment environment, ManualWhiteIpService manualWhiteIpService) {
        return new DefaultAuthPermissionService(environment, manualWhiteIpService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.addBasenames("classpath:i18n/errors/messages");
        messageSource.setDefaultLocale(Locale.CHINA);
        return messageSource;
    }

}
