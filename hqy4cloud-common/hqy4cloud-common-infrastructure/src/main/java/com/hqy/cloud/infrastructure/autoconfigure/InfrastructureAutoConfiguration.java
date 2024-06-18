package com.hqy.cloud.infrastructure.autoconfigure;

import com.hqy.cloud.infrastructure.random.RandomCodeService;
import com.hqy.cloud.infrastructure.random.RedisRandomCodeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/2
 */
@Configuration
public class InfrastructureAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RandomCodeService randomCodeService() {
        return new RedisRandomCodeService();
    }


}
