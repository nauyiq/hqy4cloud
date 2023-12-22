package com.hqy.cloud.rpc.config;

import com.hqy.cloud.rpc.core.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/20 16:28
 */
@Configuration
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class EnvironmentConfiguration {

    @Bean
    @Primary
    public Environment environmentCong() {
        return new Environment();
    }

}
