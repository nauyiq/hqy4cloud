package com.hqy.cloud.collection.autoconfigure;

import com.hqy.cloud.collection.core.CollectorHolder;
import com.hqy.cloud.collection.core.exception.ExceptionCollActionEventHandler;
import com.hqy.cloud.collection.core.exception.ExceptionCollectionConfigProperties;
import com.hqy.cloud.collection.core.exception.ExceptionCollector;
import com.hqy.cloud.collection.core.sql.SqlCollectionConfigProperties;
import com.hqy.cloud.collection.core.sql.SqlCollector;
import com.hqy.cloud.collection.core.throttles.ThrottleCollectionConfigProperties;
import com.hqy.cloud.collection.core.throttles.ThrottledCollector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/3
 */
@Configuration
public class CollectionAutoConfiguration  {

    @Bean
    public CollectorHolder collectorHolder() {
        return new CollectorHolder();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionCollector exceptionCollector(ExceptionCollectionConfigProperties properties) {
        return new ExceptionCollector(properties);
    }

    @Bean
    public ExceptionCollActionEventHandler handler() {
        return new ExceptionCollActionEventHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlCollector sqlCollector(SqlCollectionConfigProperties properties) {
        return new SqlCollector(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ThrottledCollector throttledCollector(ThrottleCollectionConfigProperties properties) {
        return new ThrottledCollector(properties);
    }



}
