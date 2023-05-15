package com.hqy4cloud.dynamics.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 多数据源配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/30 11:14
 */
@Data
@ConfigurationProperties(prefix = "spring.datasource")
@ConditionalOnProperty(name = "spring.datasource.dynamic.enabled", havingValue = "true")
public class DynamicDataSourceProperties {

    private Map<String, Map<String,String>> dbs;
    private int initialSize;
    private int minIdle;
    private int maxActive;
    private int maxWait;
    private int timeBetweenEvictionRunsMillis;
    private int minEvictableIdleTimeMillis;
    private int maxEvictableIdleTimeMillis;
    private String validationQuery;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private boolean testWhileIdle;


    public DruidDataSource dataSource(DruidDataSource druidDataSource){
        // 初始连接数
        druidDataSource.setInitialSize(initialSize);
        // 最小连接池数量
        druidDataSource.setMinIdle(minIdle);
        // 最大连接池数量
        druidDataSource.setMaxActive(maxActive);
        // 获取连接等待超时的时间
        druidDataSource.setMaxWait(maxWait);
        // 检测间隔时间，检测需要关闭的空闲连接，单位毫秒
        druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        // 一个连接在连接池中最小的生存时间，单位毫秒
        druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        // 一个连接在连接池中最大的生存时间，单位毫秒
        druidDataSource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
        // 配置检测连接是否有效
        druidDataSource.setValidationQuery(validationQuery);
        // 如果为true（默认为false），当应用向连接池申请连接时，连接池会判断这条连接是否是可用的
        druidDataSource.setTestOnBorrow(testOnBorrow);
        // 连接返回检测
        druidDataSource.setTestOnReturn(testOnReturn);
        // 失效连接检测
        druidDataSource.setTestWhileIdle(testWhileIdle);
        return druidDataSource;
    }



}
