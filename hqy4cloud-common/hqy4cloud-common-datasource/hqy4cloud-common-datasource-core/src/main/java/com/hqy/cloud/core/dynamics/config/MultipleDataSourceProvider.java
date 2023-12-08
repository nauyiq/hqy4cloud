package com.hqy.cloud.core.dynamics.config;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 多数据源提供者.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/30 11:31
 */
public interface MultipleDataSourceProvider {

    /**
     * 默认数据源
     */
    String DEFAULT_DATASOURCE = "default";

    /**
     * 加载数据源
     * @return map.
     */
    Map<String, DataSource> loadDataSource();

}
