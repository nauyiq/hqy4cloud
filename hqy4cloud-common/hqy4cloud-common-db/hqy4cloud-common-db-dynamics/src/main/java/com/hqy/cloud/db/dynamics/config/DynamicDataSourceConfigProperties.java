package com.hqy.cloud.db.dynamics.config;

import com.hqy.cloud.datasource.druid.DruidConfigProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Map;

/**
 * 多数据源配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/30 11:14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConditionalOnProperty(name = "spring.datasource.dynamic.enabled", havingValue = "true")
public class DynamicDataSourceConfigProperties extends DruidConfigProperties {

    private Map<String, Map<String,String>> dbs;




}
