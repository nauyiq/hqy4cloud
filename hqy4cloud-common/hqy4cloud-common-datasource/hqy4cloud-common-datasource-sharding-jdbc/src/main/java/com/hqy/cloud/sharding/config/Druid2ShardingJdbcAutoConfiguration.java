package com.hqy.cloud.sharding.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.spring.boot.datasource.DataSourcePropertiesSetterHolder;
import org.apache.shardingsphere.spring.boot.util.DataSourceUtil;
import org.apache.shardingsphere.spring.boot.util.PropertyUtil;
import org.apache.shardingsphere.underlying.common.config.inline.InlineExpressionParser;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.metadata.AbstractDataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * sharding jdbc多数据源配置类
 * 以master命名的数据源作为DRUID默认数据源 并且配置其事务模板TransactionTemplate
 * 如涉及到分库分表的事务，请义务使用分布式事务.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/29 14:39
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@AutoConfigureBefore(DruidDataSourceAutoConfigure.class)
@EnableConfigurationProperties(ShardingDruidProperties.class)
@ConditionalOnProperty(prefix = "spring.shardingsphere", name = "enabled", havingValue = "true")
public class Druid2ShardingJdbcAutoConfiguration {

    private final Environment environment;
    private static final String SHADING_JDBC_PREFIX = "spring.shardingsphere.datasource.";
    private final ShardingDruidProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource() throws Exception {
        String masterDataSourceName = shardingJdbcMasterDataSourceName();
        return dataSource(masterDataSourceName);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionTemplate transactionTemplate(DataSourceTransactionManager dataSourceTransactionManager) {
        return new TransactionTemplate(dataSourceTransactionManager);
    }


    @Bean
    @ConditionalOnMissingBean
    public DataSourcePoolMetadataProvider dataSourcePoolMetadataProvider(DataSource dataSource) {
        return metadata -> new DruidDataSourcePoolMetadata((DruidDataSource) dataSource, properties);
    }

    private String shardingJdbcMasterDataSourceName() {
        StandardEnvironment standardEnv = (StandardEnvironment) this.environment;
        standardEnv.setIgnoreUnresolvableNestedPlaceholders(true);
        String names = Druid2ShardingJdbcAutoConfiguration.SHADING_JDBC_PREFIX + "names";

        String nameProperty = standardEnv.getProperty(names);
        AssertUtil.notEmpty(nameProperty, "Wrong datasource properties!");
        List<String> dataSourceNames = new InlineExpressionParser(nameProperty).splitAndEvaluate();
        //取第一个数据源名称为master, 配置数据源必须记得.
        return dataSourceNames.get(0);
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private DataSource dataSource(final String dataSourceName) throws ReflectiveOperationException {
        Map dataSourceProps = PropertyUtil.handle(this.environment, Druid2ShardingJdbcAutoConfiguration.SHADING_JDBC_PREFIX + dataSourceName.trim(), Map.class);
        AssertUtil.notEmpty(dataSourceName, "Wrong datasource properties!");
        DataSource result = DataSourceUtil.getDataSource(dataSourceProps.get("type").toString(), dataSourceProps);
        DataSourcePropertiesSetterHolder.getDataSourcePropertiesSetterByType(dataSourceProps.get("type").toString())
                .ifPresent(dataSourcePropertiesSetter -> dataSourcePropertiesSetter.propertiesSet(this.environment, SHADING_JDBC_PREFIX, dataSourceName, result));

        if (result instanceof DruidDataSource) {
            // 如果是druid连接池 配置其连接信息
            properties.config((DruidDataSource) result);
        }
        return result;
    }


    public static class DruidDataSourcePoolMetadata extends AbstractDataSourcePoolMetadata<DruidDataSource> {
        private final ShardingDruidProperties properties;

        /**
         * Create an instance with the data source to use.
         * @param dataSource the data source
         */
        protected DruidDataSourcePoolMetadata(DruidDataSource dataSource, ShardingDruidProperties properties) {
            super(dataSource);
            this.properties = properties;
        }

        @Override
        public Integer getActive() {
            return properties.getMaxActive();
        }

        @Override
        public Integer getMax() {
            return properties.getMaxActive();
        }

        @Override
        public Integer getMin() {
            return properties.getMinIdle();
        }

        @Override
        public String getValidationQuery() {
            return properties.getValidationQuery();
        }

        @Override
        public Boolean getDefaultAutoCommit() {
            return null;
        }

    }




}
