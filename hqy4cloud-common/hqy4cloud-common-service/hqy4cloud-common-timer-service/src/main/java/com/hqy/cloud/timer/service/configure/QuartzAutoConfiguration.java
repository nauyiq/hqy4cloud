package com.hqy.cloud.timer.service.configure;

import com.hqy.cloud.util.config.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/7
 */
@Configuration
@PropertySource(value = "classpath:quartz-config.yml", factory = YamlPropertySourceFactory.class)
public class QuartzAutoConfiguration {




}
