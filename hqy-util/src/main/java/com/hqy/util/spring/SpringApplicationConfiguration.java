package com.hqy.util.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 13:47
 */
@Configuration
public class SpringApplicationConfiguration {

    @Bean
    public SpringContextHolder getContextHolder() {
        return new SpringContextHolder();
    }

}
