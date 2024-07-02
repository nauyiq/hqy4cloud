package com.hqy.cloud.util.autoconfigure;

import com.hqy.cloud.util.authentication.AuthorizationService;
import com.hqy.cloud.util.authentication.support.JwtAuthorizationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/1
 */
@Configuration(proxyBeanMethods = false)
public class CommonUtilAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationService authorizationService() {
        return new JwtAuthorizationService();
    }


}
