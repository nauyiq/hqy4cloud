package com.hqy.multiple.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.hqy.multiple.DynamicMultipleDataSource;
import com.hqy.multiple.MultipleDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 多数据源配置类
 * spring boot启动时务必将DataSourceAutoConfiguration给禁止掉：
   @SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-09-03 17:55
 */
@Configuration
public class DynamicDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.druid.default")
    public DataSource defaultDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public DynamicMultipleDataSource dynamicMultipleDataSource(DataSource defaultDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        String defaultSource = MultipleDataSource.DataSourceName.DEFAULT_DATA_SOURCE.name;
        targetDataSources.put(defaultSource, defaultDataSource);
        return new DynamicMultipleDataSource(defaultDataSource, targetDataSources);
    }



}
