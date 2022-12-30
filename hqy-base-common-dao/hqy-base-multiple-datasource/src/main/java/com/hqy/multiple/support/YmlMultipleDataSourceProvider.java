package com.hqy.multiple.support;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.hqy.multiple.MultipleDataSourceProvider;
import com.hqy.multiple.config.MultipleDataSourceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * yaml多数据源提供者.
 * @see MultipleDataSourceProvider
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/30 11:32
 */
@Slf4j
@RequiredArgsConstructor
public class YmlMultipleDataSourceProvider implements MultipleDataSourceProvider {

    private final MultipleDataSourceProperties multipleDataSourceProperties;

    @Override
    public Map<String, DataSource> loadDataSource() {
        Map<String, Map<String, String>> dbs = multipleDataSourceProperties.getDbs();
        Map<String, DataSource> map = new HashMap<>(dbs.size());
        try{
            for (String key: dbs.keySet()){
                DruidDataSource druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(dbs.get(key));
                map.put(key, multipleDataSourceProperties.dataSource(druidDataSource));
            }
        }catch (Exception e){
           log.error(e.getMessage(), e);
        }
        return map;
    }
}
