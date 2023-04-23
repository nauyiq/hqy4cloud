package com.hqy.cloud.seata.config;

import com.hqy.cloud.util.config.YamlPropertySourceFactory;
import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/4/21 13:36
 */
@Configuration
@EnableAutoDataSourceProxy
@PropertySource(value = "classpath:seata-config.yml", factory = YamlPropertySourceFactory.class)
public class SeataAutoConfiguration {
}
