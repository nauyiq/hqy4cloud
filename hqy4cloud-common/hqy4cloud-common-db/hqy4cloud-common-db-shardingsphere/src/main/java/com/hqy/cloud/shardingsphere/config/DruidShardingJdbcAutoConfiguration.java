package com.hqy.cloud.shardingsphere.config;

import cn.hutool.core.map.MapUtil;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidFilterConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidSpringAopConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidStatViewServletConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidWebStatFilterConfiguration;
import com.hqy.cloud.datasource.druid.config.DruidAutoConfiguration;
import lombok.SneakyThrows;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * sharding jdbc druid 数据源配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/29 14:39
 */
@Configuration
@ConditionalOnClass(DruidDataSourceAutoConfigure.class)
@AutoConfigureAfter(DruidAutoConfiguration.class)
@EnableConfigurationProperties({DruidStatProperties.class})
@ConditionalOnProperty(prefix = "spring.shardingsphere", name = "enabled", havingValue = "true")
@Import({
        DruidSpringAopConfiguration.class,
        DruidStatViewServletConfiguration.class,
        DruidWebStatFilterConfiguration.class,
        DruidFilterConfiguration.class
})
public class DruidShardingJdbcAutoConfiguration implements SmartInitializingSingleton, BeanFactoryAware {
    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    @Override
    public void setBeanFactory(@Nonnull BeanFactory beanFactory) throws BeansException {
        this.configurableListableBeanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }


    @Override
    @SneakyThrows
    public void afterSingletonsInstantiated() {
        DataSource dataSource = configurableListableBeanFactory.getBean(DataSource.class);
        if (dataSource instanceof ShardingDataSource shardingDataSource) {
            Map<String, DataSource> dataSourceMap = shardingDataSource.getDataSourceMap();
            if (MapUtil.isNotEmpty(dataSourceMap)) {
                Collection<DataSource> dataSources = dataSourceMap.values();
                Map<String, Filter> filterMap = configurableListableBeanFactory.getBeansOfType(Filter.class);
                if (MapUtil.isNotEmpty(filterMap)) {
                    for (DataSource source : dataSources) {
                        if (source instanceof DruidDataSource druidDataSource) {
                            druidDataSource.setProxyFilters(new ArrayList<>(filterMap.values()));
                            if (!druidDataSource.isInited()) {
                                druidDataSource.init();
                            }
                        }
                    }
                }
            }
        }
    }
}
