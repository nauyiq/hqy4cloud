package com.hqy.multiple;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 动态数据源
 * @author qiyuan.hong
 * @date 2021-09-03 17:41
 */
public class DynamicMultipleDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> DATASOURCE_KEY = new InheritableThreadLocal<>();

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
        DATASOURCE_KEY.set(dataSource);
    }

    public static void clearDataSource() {
        DATASOURCE_KEY.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DATASOURCE_KEY.get();
    }
}
