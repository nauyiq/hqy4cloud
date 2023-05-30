package com.hqy4cloud.dynamics.support;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.hqy4cloud.dynamics.config.MultipleDataSourceProvider;
import com.hqy4cloud.dynamics.config.DynamicDataSourceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * druid多数据源提供者.
 * @see MultipleDataSourceProvider
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/30 11:32
 */
@Slf4j
@RequiredArgsConstructor
public class DruidMultipleDataSourceProvider implements MultipleDataSourceProvider {
    private final DataSourceProperties dataSourceProperties;
    private final DynamicDataSourceProperties properties;

    @Override
    public Map<String, DataSource> loadDataSource() {
        Map<String, Map<String, String>> dbs = properties.getDbs();
        Map<String, DataSource> map = new HashMap<>(dbs.size());
        try{
            //加载默认数据源
            Map<String, String> propertiesMap = properties.setDbProperties(dataSourceProperties);
            DataSource defaultDataSource = DruidDataSourceFactory.createDataSource(propertiesMap);
            map.put(DEFAULT_DATASOURCE, defaultDataSource);

            //加载其他多数据源配置.
            for (Map.Entry<String, Map<String, String>> entry : dbs.entrySet()) {
                String key = entry.getKey();
                //覆盖默认配置. 比如覆盖db连接信息等.
                propertiesMap.putAll(entry.getValue());
                DataSource dataSource = DruidDataSourceFactory.createDataSource(propertiesMap);
                map.put(key, dataSource);
            }
        } catch (Exception e){
           log.error(e.getMessage(), e);
        }

        return map;
    }
}
