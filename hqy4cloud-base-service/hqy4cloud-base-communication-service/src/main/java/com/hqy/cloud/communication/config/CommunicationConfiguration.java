package com.hqy.cloud.communication.config;

import com.hqy.cloud.communication.sms.config.SmsProperties;
import com.hqy.cloud.communication.sms.core.SmsSender;
import com.hqy.cloud.communication.sms.core.support.DefaultSmsSender;
import com.hqy.cloud.communication.sms.core.support.MockSmsSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/12
 */
@Configuration
@EnableConfigurationProperties(value = SmsProperties.class)
public class CommunicationConfiguration {

    @Bean
    @Profile({"test", "prod"})
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = SmsProperties.PREFIX, value = "enabled", havingValue = "true")
    public SmsSender smsSender(SmsProperties properties) {
        return new DefaultSmsSender(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SmsSender mockSmsSender() {
        return new MockSmsSender();
    }


}
