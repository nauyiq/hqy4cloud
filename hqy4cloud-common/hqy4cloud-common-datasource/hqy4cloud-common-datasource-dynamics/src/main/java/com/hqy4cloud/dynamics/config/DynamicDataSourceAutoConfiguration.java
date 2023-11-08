package com.hqy4cloud.dynamics.config;

import com.hqy4cloud.dynamics.support.DynamicMultipleDataSource;
import com.hqy4cloud.dynamics.support.MultipleDataSourceAop;
import com.hqy4cloud.dynamics.support.DruidMultipleDataSourceProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 动态数据源配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/30 14:17
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
public class DynamicDataSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MultipleDataSourceProvider multipleDataSourceProvider(DynamicDataSourceProperties dynamicDataSourceProperties, DataSourceProperties dataSourceProperties) {
        return new DruidMultipleDataSourceProvider(dataSourceProperties, dynamicDataSourceProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicMultipleDataSource dynamicMultipleDataSource(MultipleDataSourceProvider multipleDataSourceProvider) {
        return new DynamicMultipleDataSource(multipleDataSourceProvider);
    }

    @Bean
    public MultipleDataSourceAop dataSourceAop() {
        return new MultipleDataSourceAop();
    }

}
