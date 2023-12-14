package com.hqy.cloud.db.dynamics.support;

import com.hqy.cloud.db.dynamics.config.MultipleDataSourceProvider;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源
 * @author qiyuan.hong
 * @date 2021-09-03 17:41
 */
public class DynamicMultipleDataSource extends AbstractRoutingDataSource {

    public DynamicMultipleDataSource(MultipleDataSourceProvider multipleDataSourceProvider) {
        Map<Object, Object> targetDataSources = new HashMap<>(multipleDataSourceProvider.loadDataSource());
        super.setTargetDataSources(targetDataSources);
        super.setDefaultTargetDataSource(targetDataSources.get(MultipleDataSourceProvider.DEFAULT_DATASOURCE));
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicMultipleDataSourceContextHolder.getDataSourceName();
    }
}
