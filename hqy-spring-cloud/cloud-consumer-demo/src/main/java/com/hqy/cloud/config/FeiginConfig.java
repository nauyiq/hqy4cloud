package com.hqy.cloud.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qy
 * @create 2021/7/22 21:33
 */
@Configuration
public class FeiginConfig {

    @Bean
    Logger.Level feignLoggerLevelSetting() {
        return Logger.Level.FULL;
    }


}
