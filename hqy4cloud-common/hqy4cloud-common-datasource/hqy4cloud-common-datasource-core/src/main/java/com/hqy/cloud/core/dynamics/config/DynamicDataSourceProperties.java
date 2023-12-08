package com.hqy.cloud.core.dynamics.config;

import com.hqy.cloud.druid.DruidProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "spring.datasource.druid")
@ConditionalOnProperty(name = "spring.datasource.dynamic.enabled", havingValue = "true")
public class DynamicDataSourceProperties extends DruidProperties {

    private Map<String, Map<String,String>> dbs;




}
