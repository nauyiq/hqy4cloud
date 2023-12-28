package com.hqy.cloud.auth.config;

import com.hqy.cloud.auth.core.authentication.AuthPermissionService;
import com.hqy.cloud.auth.core.authentication.support.AuthenticationAspect;
import com.hqy.cloud.auth.core.authentication.support.AuthenticationCacheService;
import com.hqy.cloud.auth.core.authentication.support.DefaultAuthPermissionService;
import com.hqy.cloud.auth.limit.support.BiBlockedIpRedisService;
import com.hqy.cloud.auth.limit.support.ManualBlockedIpService;
import com.hqy.cloud.auth.limit.support.ManualWhiteIpRedisService;
import com.hqy.foundation.limit.service.ManualWhiteIpService;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

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
    public ManualWhiteIpService manualWhiteIpService(RedissonClient redisson) {
        return new ManualWhiteIpRedisService(redisson);
    }

    @Bean
    @Lazy
    @ConditionalOnMissingBean
    public BiBlockedIpRedisService biBlockedIpRedisService(RedissonClient redissonClient) {
        return new BiBlockedIpRedisService(redissonClient);
    }

    @Bean
    @Lazy
    @ConditionalOnMissingBean
    public ManualBlockedIpService manualBlockedIpService(RedissonClient redissonClient) {
        return new ManualBlockedIpService(redissonClient);
    }

    @Bean
    public AuthenticationCacheService resourceInRoleCacheServer(RedissonClient redissonClient) {
        return new AuthenticationCacheService(redissonClient);
    }

    @Bean
    public AuthPermissionService authPermissionService(AuthenticationCacheService authenticationCacheService, ManualWhiteIpService manualWhiteIpService) {
        return new DefaultAuthPermissionService(authenticationCacheService, manualWhiteIpService);
    }

    @Bean
    public AuthenticationAspect authenticationAspect(AuthPermissionService authPermissionService) {
        return new AuthenticationAspect(authPermissionService);
    }

    @Bean
    public MessageSource securityMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.addBasenames("classpath:i18n/errors/messages");
        messageSource.setDefaultLocale(Locale.CHINA);
        return messageSource;
    }



}
