package com.hqy.cloud.shardingsphere.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallFilter;
import com.hqy.cloud.datasource.druid.filter.FilterFactory;
import com.hqy.cloud.datasource.druid.filter.StatFilterConfig;
import org.apache.shardingsphere.spring.boot.datasource.DataSourcePropertiesSetter;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/14 17:58
 */
public class DruidDataSourcePropertiesSetter implements DataSourcePropertiesSetter {

    @Override
    public void propertiesSet(Environment environment, String prefix, String dataSourceName, DataSource dataSource) {
        if (dataSource instanceof DruidDataSource druidDataSource) {
            List<Filter> filters = new ArrayList<>();
            StatFilter statFilter = FilterFactory.createStatFilter(environment);
            if (statFilter != null) {
                filters.add(statFilter);
            }
            Slf4jLogFilter slf4jLogFilter = FilterFactory.createSlf4jLogFilter(environment);
            if (slf4jLogFilter != null) {
                filters.add(slf4jLogFilter);
            }
            WallFilter wallFilter = FilterFactory.createWallFilter(environment);
            if (wallFilter != null) {
                filters.add(wallFilter);
            }
            druidDataSource.setProxyFilters(filters);
        }
    }

    @Override
    public String getType() {
        return DruidDataSource.class.getName();
    }
}
