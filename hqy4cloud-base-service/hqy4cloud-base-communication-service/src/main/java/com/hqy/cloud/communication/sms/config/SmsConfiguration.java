package com.hqy.cloud.communication.sms.config;

import com.hqy.cloud.communication.sms.core.SmsSender;
import com.hqy.cloud.communication.sms.core.support.DefaultSmsSender;
import com.hqy.cloud.communication.sms.core.support.MockSmsSender;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hongqy
 * @date 2026/1/19
 */
@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsConfiguration {

    @Bean
    public SmsSender smsSender(SmsProperties smsProperties) {
        if (smsProperties.isMock()) {
            return new MockSmsSender();
        }
        return new DefaultSmsSender(smsProperties);
    }


}
