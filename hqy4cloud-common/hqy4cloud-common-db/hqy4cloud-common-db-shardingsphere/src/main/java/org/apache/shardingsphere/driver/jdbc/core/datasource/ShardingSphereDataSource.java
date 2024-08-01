package org.apache.shardingsphere.driver.jdbc.core.datasource;

import org.apache.shardingsphere.driver.jdbc.adapter.AbstractDataSourceAdapter;
import org.apache.shardingsphere.driver.jdbc.context.JDBCContext;
import org.apache.shardingsphere.driver.state.DriverStateContext;
import org.apache.shardingsphere.infra.config.database.impl.DataSourceProvidedDatabaseConfiguration;
import org.apache.shardingsphere.infra.config.mode.ModeConfiguration;
import org.apache.shardingsphere.infra.config.rule.RuleConfiguration;
import org.apache.shardingsphere.infra.config.rule.scope.GlobalRuleConfiguration;
import org.apache.shardingsphere.infra.instance.metadata.InstanceMetaData;
import org.apache.shardingsphere.infra.instance.metadata.InstanceMetaDataBuilderFactory;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.mode.manager.ContextManagerBuilderFactory;
import org.apache.shardingsphere.mode.manager.ContextManagerBuilderParameter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 解决cglib无法代理final类
 * @author qiyuan.hong
 * @date 2024/7/29
 */
public class ShardingSphereDataSource extends AbstractDataSourceAdapter implements AutoCloseable{
    private final String databaseName;

    private final ContextManager contextManager;

    private final JDBCContext jdbcContext;

    public ShardingSphereDataSource(final String databaseName, final ModeConfiguration modeConfig) throws SQLException {
        this.databaseName = databaseName;
        contextManager = createContextManager(databaseName, modeConfig, new HashMap<>(), new LinkedList<>(), new Properties());
        jdbcContext = new JDBCContext(contextManager.getDataSourceMap(databaseName));
    }

    public ShardingSphereDataSource(final String databaseName, final ModeConfiguration modeConfig, final Map<String, DataSource> dataSourceMap,
                                    final Collection<RuleConfiguration> ruleConfigs, final Properties props) throws SQLException {
        this.databaseName = databaseName;
        contextManager = createContextManager(databaseName, modeConfig, dataSourceMap, ruleConfigs, null == props ? new Properties() : props);
        jdbcContext = new JDBCContext(contextManager.getDataSourceMap(databaseName));
    }

    private ContextManager createContextManager(final String databaseName, final ModeConfiguration modeConfig, final Map<String, DataSource> dataSourceMap,
                                                final Collection<RuleConfiguration> ruleConfigs, final Properties props) throws SQLException {
        InstanceMetaData instanceMetaData = InstanceMetaDataBuilderFactory.create("JDBC", -1);
        Collection<RuleConfiguration> globalRuleConfigs = ruleConfigs.stream().filter(each -> each instanceof GlobalRuleConfiguration).collect(Collectors.toList());
        Collection<RuleConfiguration> databaseRuleConfigs = new LinkedList<>(ruleConfigs);
        databaseRuleConfigs.removeAll(globalRuleConfigs);
        ContextManagerBuilderParameter parameter = new ContextManagerBuilderParameter(modeConfig, Collections.singletonMap(databaseName,
                new DataSourceProvidedDatabaseConfiguration(dataSourceMap, databaseRuleConfigs)), globalRuleConfigs, props, Collections.emptyList(), instanceMetaData, false);
        return ContextManagerBuilderFactory.getInstance(modeConfig).build(parameter);
    }

    @Override
    public Connection getConnection() {
        return DriverStateContext.getConnection(databaseName, contextManager, jdbcContext);
    }

    @Override
    public Connection getConnection(final String username, final String password) {
        return getConnection();
    }

    /**
     * Close data sources.
     *
     * @param dataSourceNames data source names to be closed
     * @throws Exception exception
     */
    public void close(final Collection<String> dataSourceNames) throws Exception {
        Map<String, DataSource> dataSourceMap = contextManager.getDataSourceMap(databaseName);
        for (String each : dataSourceNames) {
            close(dataSourceMap.get(each));
        }
        contextManager.close();
    }

    private void close(final DataSource dataSource) throws Exception {
        if (dataSource instanceof AutoCloseable) {
            ((AutoCloseable) dataSource).close();
        }
    }

    @Override
    public void close() throws Exception {
        close(contextManager.getDataSourceMap(databaseName).keySet());
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        Map<String, DataSource> dataSourceMap = contextManager.getDataSourceMap(databaseName);
        return dataSourceMap.isEmpty() ? 0 : dataSourceMap.values().iterator().next().getLoginTimeout();
    }

    @Override
    public void setLoginTimeout(final int seconds) throws SQLException {
        for (DataSource each : contextManager.getDataSourceMap(databaseName).values()) {
            each.setLoginTimeout(seconds);
        }
    }
}
