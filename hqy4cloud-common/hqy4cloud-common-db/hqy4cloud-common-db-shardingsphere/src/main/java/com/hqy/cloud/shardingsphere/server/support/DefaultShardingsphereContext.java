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
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.core.rule.TableRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.context.ShardingRuntimeContext;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.apache.shardingsphere.underlying.common.exception.ShardingSphereException;
import org.springframework.boot.CommandLineRunner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
        modifierTableActualNodeLock.lock();
        try {
            // 获取分表规则对象
            TableRule tableRule = getTableRule(logicTableName);
            // 真实数据库节点.
            Collection<String> actualDatasourceNames = tableRule.getActualDatasourceNames();
            for (String datasourceName : actualDatasourceNames) {
                Collection<String> actualTableNames = tableRule.getActualTableNames(datasourceName);
                // 获取数据库中所有真实的表
                Set<String> actualNodes = getStartWithLogicTableNameActualNodes(datasourceName, logicTableName);
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
    public void loadTableActualNodes(String logicTableName, NodeNameMatchingFunction matching) {
        modifierTableActualNodeLock.lock();
        try {
            // 获取分表规则对象
            TableRule tableRule = getTableRule(logicTableName);
            // 真实数据库节点.
            Collection<String> actualDatasourceNames = tableRule.getActualDatasourceNames();
            for (String datasourceName : actualDatasourceNames) {
                // shardingsphere上下文中加载的所有真实节点数据表.
                Collection<String> actualTableNames = tableRule.getActualTableNames(datasourceName);
                // 获取数据库中所有真实的表, 并且matching函数匹配的表
                Set<String> actualNodes = jdbcContext.getTables(datasourceName).stream().
                        filter(table -> table.startsWith(logicTableName) && matching.isMatching(table)).collect(Collectors.toSet());
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
            CreateTableSql createTableSql = commonDbService.selectTableCreateSql(logicTableName);
            if (createTableSql == null) {
                throw new ShardingSphereException("Failed execute to auto create table: "
                        + actualTableName + ", because not found " + logicTableName + " ddl");
            }
            // 创建表
            String createTable = createTableSql.getCreateTable()
                    .replace("CREATE TABLE", "CREATE TABLE IF NOT EXISTS")
                    .replace(logicTableName, actualTableName);
            commonDbService.execute(createTable);
            // 获取分表规则对象
            TableRule tableRule = getTableRule(logicTableName);
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



    private TableRule getTableRule(String logicTableName) {
        ShardingDataSource shardingDataSource = jdbcContext.getShardingDateSource();
        ShardingRuntimeContext runtimeContext = shardingDataSource.getRuntimeContext();
        ShardingRule shardingRule = runtimeContext.getRule();
        return shardingRule.getTableRule(logicTableName);

    }

    private Set<String> getStartWithLogicTableNameActualNodes(String datasourceName, String logicTableName) {
        return jdbcContext.getTables(datasourceName).stream().
                filter(table -> table.startsWith(logicTableName)).collect(Collectors.toSet());
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
