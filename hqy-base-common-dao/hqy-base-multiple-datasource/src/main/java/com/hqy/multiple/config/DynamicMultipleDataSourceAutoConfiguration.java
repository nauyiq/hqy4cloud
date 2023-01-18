package com.hqy.multiple.config;

import com.hqy.multiple.MultipleDataSourceProvider;
import com.hqy.multiple.support.DynamicMultipleDataSource;
import com.hqy.multiple.support.YmlMultipleDataSourceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 动态数据源配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/30 14:17
 */
@Configuration
@RequiredArgsConstructor
public class DynamicMultipleDataSourceAutoConfiguration {

    private final MultipleDataSourceProperties multipleDataSourceProperties;

    @Bean
    @ConditionalOnMissingBean
    public MultipleDataSourceProvider multipleDataSourceProvider() {
        return new YmlMultipleDataSourceProvider(multipleDataSourceProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicMultipleDataSource dynamicMultipleDataSource() {
        return new DynamicMultipleDataSource(multipleDataSourceProvider());
    }



}
