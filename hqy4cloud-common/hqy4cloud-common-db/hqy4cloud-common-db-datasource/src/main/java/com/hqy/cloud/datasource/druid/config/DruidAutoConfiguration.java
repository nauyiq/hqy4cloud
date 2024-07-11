package com.hqy.cloud.datasource.druid.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.hqy.cloud.datasource.druid.DruidConfigProperties;
import com.hqy.cloud.datasource.druid.filter.ExtendDruidStatFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.hqy.cloud.datasource.druid.DruidConstants.FILTER_STAT_PREFIX;

/**
 * druid通用配置
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/13 10:38
 */
@Configuration
@EnableConfigurationProperties({DataSourceProperties.class, DruidConfigProperties.class})
public class DruidAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(FILTER_STAT_PREFIX)
    @ConditionalOnProperty(prefix = FILTER_STAT_PREFIX, name = "enabled")
    public StatFilter statFilter(DruidConfigProperties druidProperties) {
        return new ExtendDruidStatFilter(druidProperties);
    }




}
