package com.hqy.cloud.shardingsphere.autoconfigure;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidFilterConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidSpringAopConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidStatViewServletConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidWebStatFilterConfiguration;
import com.hqy.cloud.datasource.druid.config.DruidAutoConfiguration;
import com.hqy.cloud.db.service.CommonDbService;
import com.hqy.cloud.shardingsphere.server.ShardingJdbcContext;
import com.hqy.cloud.shardingsphere.server.ShardingsphereContext;
import com.hqy.cloud.shardingsphere.server.support.DefaultShardingJdbcContext;
import com.hqy.cloud.shardingsphere.server.support.DefaultShardingsphereContext;
import com.hqy.cloud.shardingsphere.service.ShardingCommonDbService;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * sharding jdbc druid 数据源配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/29 14:39
 */
@Configuration
@ConditionalOnClass(DruidDataSourceAutoConfigure.class)
@AutoConfigureAfter(DruidAutoConfiguration.class)
@EnableConfigurationProperties({DruidStatProperties.class})
@ConditionalOnProperty(prefix = "spring.shardingsphere", name = "enabled", havingValue = "true")
@Import({
        DruidSpringAopConfiguration.class,
        DruidStatViewServletConfiguration.class,
        DruidWebStatFilterConfiguration.class,
        DruidFilterConfiguration.class
})
public class DruidShardingJdbcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ShardingJdbcContext shardingJdbcContext(JdbcTemplate jdbcTemplate) {
        return new DefaultShardingJdbcContext();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public CommonDbService commonDbService(ShardingJdbcContext context) {
        return new ShardingCommonDbService(context);
    }

    @Bean
    @ConditionalOnMissingBean
    public ShardingsphereContext shardingsphereContext(ShardingJdbcContext shardingJdbcContext, CommonDbService commonDbService) {
        return new DefaultShardingsphereContext(shardingJdbcContext, commonDbService);
    }




}
