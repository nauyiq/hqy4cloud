package com.hqy.cloud.auth.infrastructure.certification.config;

import com.hqy.cloud.auth.infrastructure.certification.service.AuthService;
import com.hqy.cloud.auth.infrastructure.certification.service.support.AuthServiceImpl;
import com.hqy.cloud.auth.infrastructure.certification.service.support.MockAuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author hongqy
 * @date 2025/2/14
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(CertificationConfigProperties.class)
public class CertificationAutoConfiguration {
    private final CertificationConfigProperties properties;
    @Bean
    @ConditionalOnMissingBean
    @Profile({"default", "prod"})
    public AuthService authService(CertificationConfigProperties properties) {
        return new AuthServiceImpl(properties);
    }
    @Bean
    @ConditionalOnMissingBean
    @Profile({"dev","test"})
    public AuthService mockAuthService() {
        return new MockAuthServiceImpl();
    }


}
