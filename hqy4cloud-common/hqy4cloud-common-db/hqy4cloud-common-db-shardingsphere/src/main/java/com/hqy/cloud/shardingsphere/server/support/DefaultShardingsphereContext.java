package com.hqy.cloud.shardingsphere.server.support;

import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.db.common.CreateTableSql;
import com.hqy.cloud.db.service.CommonDbService;
import com.hqy.cloud.shardingsphere.algorithm.NodeNameMatchingFunction;
import com.hqy.cloud.shardingsphere.server.ShardingJdbcContext;
import com.hqy.cloud.shardingsphere.server.ShardingsphereContext;
import com.hqy.cloud.util.ReflectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.shardingsphere.core.rule.MasterSlaveRule;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.core.rule.TableRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.context.ShardingRuntimeContext;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.apache.shardingsphere.underlying.common.exception.ShardingSphereException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/4
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultShardingsphereContext implements ShardingsphereContext, CommandLineRunner {
    private final ShardingJdbcContext jdbcContext;
    private final CommonDbService commonDbService;
    private final Lock modifierTableActualNodeLock = new ReentrantLock();

    @Override
    public void loadTableActualNodes(String logicTableName) {
        this.loadTableActualNodes(logicTableName, (name) -> true);
    }


    @Override
    public void loadTableActualNodes(String logicTableName, NodeNameMatchingFunction matching) {
        modifierTableActualNodeLock.lock();
        try {
            ShardingRule shardingRule = getShardingRule();
            if (shardingRule == null) {
                // 如果分表规则为空, 说明没有配置分表规则，因此不需要加载分表的真实节点列表. 直接return
                log.warn("Not found sharding rule, logic table name {}.", logicTableName);
                return;
            }
            Map<String, String> namesMap = shardingRule.getMasterSlaveRules().stream().collect(Collectors.toMap(MasterSlaveRule::getName, MasterSlaveRule::getMasterDataSourceName));
            TableRule tableRule = shardingRule.getTableRule(logicTableName);
            // 真实数据库节点.
            Collection<String> actualDatasourceNames = tableRule.getActualDatasourceNames();
            for (String datasourceName : actualDatasourceNames) {
                // shardingsphere上下文中加载的所有真实节点数据表.
                Collection<String> actualTableNames = tableRule.getActualTableNames(datasourceName);
                Set<String> actualNodes = getActualNodes(logicTableName, matching, namesMap, datasourceName);
                if ((CollectionUtils.isEmpty(actualNodes) && CommonSwitcher.ENABLE_SUING_LOGIC_TABLE_WHEN_ACTUAL_NODES_EMPTY.isOff()) ||
                        !actualTableNames.containsAll(actualNodes)) {
                    // 刷新所有节点表
                    reloadDatasourceActualTables(tableRule, datasourceName, actualNodes);
                }
            }
        } finally {
            modifierTableActualNodeLock.unlock();
        }
    }



    @Override
    public void addTableActualNode(String logicTableName, String actualTableName) {
        modifierTableActualNodeLock.lock();
        try {
            ShardingRule shardingRule = getShardingRule();
            if (shardingRule == null) {
                // 如果分表规则为空, 说明没有配置分表规则，因此不需要加载分表的真实节点列表. 直接return
                log.warn("Not found sharding rule, logic table name {}.", logicTableName);
                return;
            }
            CreateTableSql createTableSql = commonDbService.selectTableCreateSql(logicTableName);
            if (createTableSql == null) {
                throw new ShardingSphereException("Failed execute to auto create table: "
                        + actualTableName + ", because not found " + logicTableName + " ddl");
            }
            // 生成创建表语句
            String createTable = createTableSql.getCreateTable()
                    .replace("CREATE TABLE", "CREATE TABLE IF NOT EXISTS")
                    .replace(logicTableName, actualTableName);

            Collection<MasterSlaveRule> rules = shardingRule.getMasterSlaveRules();
            if (CollectionUtils.isNotEmpty(rules)) {
                // 只需在主库上运行创建表DDL
                for (MasterSlaveRule masterSlaveRule : rules) {
                    String dataSourceName = masterSlaveRule.getMasterDataSourceName();
                    JdbcTemplate jdbcTemplate = jdbcContext.getJdbcTemplate(dataSourceName);
                    jdbcTemplate.execute(createTable);
                }
            } else {
                // 在所有配置的数据源上运行创建表DDL
                jdbcContext.getActualJdbcTemplates().forEach(jdbcContext -> jdbcContext.execute(createTable));
            }

            TableRule tableRule = shardingRule.getTableRule(logicTableName);
            // 真实数据库节点.
            Collection<String> actualDatasourceNames = tableRule.getActualDatasourceNames();
            for (String datasourceName : actualDatasourceNames) {
                Collection<String> actualTableNames = tableRule.getActualTableNames(datasourceName);
                addDatasourceActualTables(tableRule, datasourceName, actualTableName);
            }
            jdbcContext.addTableName(actualTableName);
        } finally {
            modifierTableActualNodeLock.unlock();
        }
    }




    /**
     * 获取shardingsphere 分表的表规则
     * @param logicTableName 逻辑表明
     * @return               {@link TableRule} 分表规则.
     */
    private TableRule getTableRule(String logicTableName) {
        ShardingRule shardingRule = getShardingRule();
        return shardingRule == null ? null : shardingRule.getTableRule(logicTableName);
    }

    private ShardingRule getShardingRule() {
        Set<JdbcTemplate> templates = jdbcContext.getContextTemplates();
        List<DataSource> dataSources = templates.stream().map(JdbcTemplate::getDataSource).toList();
        for (DataSource dataSource : dataSources) {
            if (dataSource instanceof ShardingDataSource shardingDataSource) {
                ShardingRuntimeContext runtimeContext = shardingDataSource.getRuntimeContext();
                return runtimeContext.getRule();
            }
        }
        return null;
    }

    private Set<String> getActualNodes(String logicTableName, NodeNameMatchingFunction matching, Map<String, String> namesMap, String datasourceName) {
        Set<String> tables = namesMap.containsKey(datasourceName) ? jdbcContext.getTables(namesMap.get(datasourceName)) : jdbcContext.getTables(datasourceName);
        // 获取数据库中所有真实的表, 并且matching函数匹配的表
        return tables.stream().
                filter(table -> table.startsWith(logicTableName) && matching.isMatching(table)).collect(Collectors.toSet());
    }


    private void reloadDatasourceActualTables(TableRule tableRule, String datasourceName, Set<String> allTables) {
        Map<String, Set<String>> tablesMap = getReflectTableRuleDataSourceToTablesMap(tableRule);
        if (MapUtils.isNotEmpty(tablesMap)) {
            tablesMap.put(datasourceName, allTables);
        }
    }

    private void addDatasourceActualTables(TableRule tableRule, String datasourceName, String actualTableName) {
        Map<String, Set<String>> tablesMap = getReflectTableRuleDataSourceToTablesMap(tableRule);
        Set<String> tables = tablesMap.computeIfAbsent(datasourceName, v -> new HashSet<>());
        tables.add(actualTableName);
    }

    private static Map<String, Set<String>> getReflectTableRuleDataSourceToTablesMap(TableRule tableRule) {
         return ReflectUtils.getObjectField(tableRule, "datasourceToTablesMap");
    }


    @Override
    public ShardingJdbcContext getJdbcContext() {
        return jdbcContext;
    }

    @Override
    public CommonDbService getCommonDbService() {
        return commonDbService;
    }

    @Override
    public void run(String... args) throws Exception {
        // 初始表
        if (MapUtils.isNotEmpty(INITIALIZE_TABLES)) {
            for (Map.Entry<String, NodeNameMatchingFunction> entry : INITIALIZE_TABLES.entrySet()) {
                this.loadTableActualNodes(entry.getKey(), entry.getValue());
            }
        }
    }


}
