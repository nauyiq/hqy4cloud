package com.hqy.multiple;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-09-03 17:41
 */
public class DynamicMultipleDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> dataSourceKey = new InheritableThreadLocal<>();

    public DynamicMultipleDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSource) {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSource);
        super.afterPropertiesSet();
    }

    /**
     * 设置数据源
     * @param dataSource
     */
    public static void setDataSourceKey(String dataSource) {
        dataSourceKey.set(dataSource);
    }

    public static void clearDataSource() {
        dataSourceKey.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return dataSourceKey.get();
    }
}
