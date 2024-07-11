package com.hqy.cloud.sharding.autoconfig;

import com.hqy.cloud.sharding.service.ShardingService;
import com.hqy.cloud.sharding.service.support.ShardingServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * sharding jdbc druid 数据源配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/29 14:39
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.shardingsphere", name = "enabled", havingValue = "true")
public class DruidShardingJdbcAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public ShardingService shardingsphereContext() {
        return new ShardingServiceImpl();
    }


}
