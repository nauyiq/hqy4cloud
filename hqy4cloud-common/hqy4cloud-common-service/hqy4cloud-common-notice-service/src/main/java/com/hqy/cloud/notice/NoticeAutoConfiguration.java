package com.hqy.cloud.notice;

import com.hqy.cloud.notice.email.EmailNotifier;
import com.hqy.cloud.notice.email.EmailNotifierConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/20 11:29
 */
@Configuration
@EnableConfigurationProperties({EmailNotifierConfig.class})
public class NoticeAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public EmailNotifier emailNotifier(Environment environment, EmailNotifierConfig config) {
        return new EmailNotifier(environment, config);
    }


}
