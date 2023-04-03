package com.hqy.cloud.util.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 13:47
 */
@Configuration
public class SpringApplicationConfiguration {

    @Bean
    @Primary
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SpringContextHolder getContextHolder() {
        return new SpringContextHolder();
    }

}
