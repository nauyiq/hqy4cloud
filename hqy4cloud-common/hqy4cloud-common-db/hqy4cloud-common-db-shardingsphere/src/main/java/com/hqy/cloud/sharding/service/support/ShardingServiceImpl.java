package com.hqy.cloud.sharding.service.support;

import cn.hutool.extra.spring.SpringUtil;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.db.common.CreateTableSql;
import com.hqy.cloud.db.common.SqlConstants;
import com.hqy.cloud.sharding.algorithm.NodeNameMatchingFunction;
import com.hqy.cloud.sharding.service.ShardingService;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.ReflectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.shardingsphere.core.rule.MasterSlaveRule;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.core.rule.TableRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.adapter.AbstractDataSourceAdapter;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.context.ShardingRuntimeContext;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.apache.shardingsphere.underlying.common.exception.ShardingSphereException;
import org.springframework.beans.factory.annotation.Value;
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
public class ShardingServiceImpl implements ShardingService, CommandLineRunner {

    /**
     * 默认的sharding数据连接池名称
     */
    @Value("${spring.shardingsphere.sharding.default-data-source-name:master0}")
    private String dataSourceName;

    /**
     * 从spring容器中获取到的所有JdbcTemplate的集合.
     */
    private final List<JdbcTemplate> contextJdbcTemplates = new ArrayList<>();

    /**
     * 从sharding上下文中获取的所有真实的jdbcTemplate
     */
    private final Map<String, JdbcTemplate> shardingJdbcTemplates = new HashMap<>();

    /**
     * 数据库表集合，key = 数据库名， value = 数据库中所有的表名
     */
    private final Map<String, Set<String>> actualTableNames = new HashMap<>();


    private final Lock modifierTableActualNodeLock = new ReentrantLock();



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
            CreateTableSql createTableSql = this.selectTableCreateSql(logicTableName);
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
                    JdbcTemplate template = shardingJdbcTemplates.get(dataSourceName);
                    if (template != null) {
                        template.execute(createTable);
                    } else {
                        log.warn("Not found main database jdbc template for {}.", dataSourceName);
                    }
                }
            } else {
                // 在所有配置的数据源上运行创建表DDL
                shardingJdbcTemplates.values().forEach(jdbcTemplate -> jdbcTemplate.execute(createTable));
            }

            TableRule tableRule = shardingRule.getTableRule(logicTableName);
            // 真实数据库节点.
            Collection<String> actualDatasourceNames = tableRule.getActualDatasourceNames();
            for (String datasourceName : actualDatasourceNames) {
                Collection<String> actualTableNames = tableRule.getActualTableNames(datasourceName);
                addDatasourceActualTables(tableRule, datasourceName, actualTableName);
            }
            actualTableNames.values().forEach(tables -> tables.add(actualTableName));
        } finally {
            modifierTableActualNodeLock.unlock();
        }
    }

    @Override
    public Map<String, Set<String>> getAllTables() {
        return this.actualTableNames;
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
        List<DataSource> dataSources = contextJdbcTemplates.stream().map(JdbcTemplate::getDataSource).toList();
        for (DataSource dataSource : dataSources) {
            if (dataSource instanceof ShardingDataSource shardingDataSource) {
                ShardingRuntimeContext runtimeContext = shardingDataSource.getRuntimeContext();
                return runtimeContext.getRule();
            }
        }
        return null;
    }

    private Set<String> getActualNodes(String logicTableName, NodeNameMatchingFunction matching, Map<String, String> namesMap, String datasourceName) {
        Set<String> tables = namesMap.containsKey(datasourceName) ? actualTableNames.get(namesMap.get(datasourceName)) : actualTableNames.get(datasourceName);
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
    public void run(String... args) throws Exception {
        Map<String, JdbcTemplate> jdbcTemplateMap = SpringUtil.getBeansOfType(JdbcTemplate.class);
        if (MapUtils.isNotEmpty(jdbcTemplateMap)) {
            this.contextJdbcTemplates.addAll(jdbcTemplateMap.values());
            this.contextJdbcTemplates.forEach(template -> {
                DataSource dataSource = template.getDataSource();
                if (dataSource instanceof AbstractDataSourceAdapter adapter) {
                    // 获取sharding对应内置的datasource
                    Map<String, DataSource> dataSourceMap = adapter.getDataSourceMap();
                    dataSourceMap.forEach((key, value) -> {
                        // 创建JdbcTemplate
                        JdbcTemplate jdbcTemplate = new JdbcTemplate(value);
                        shardingJdbcTemplates.put(key, jdbcTemplate);
                        // 加载每个数据源的真实表到内存中.
                        String database = jdbcTemplate.queryForObject(SqlConstants.GET_USING_DATABASE_NAME, String.class);
                        List<String> tables = jdbcTemplate.queryForList(SqlConstants.getSelectAllTableNameByDbName(database), String.class);
                        actualTableNames.put(database, new HashSet<>(tables));
                    });
                }
            });
        }

        // 初始表
        if (MapUtils.isNotEmpty(INITIALIZE_TABLES)) {
            for (Map.Entry<String, NodeNameMatchingFunction> entry : INITIALIZE_TABLES.entrySet()) {
                // 加载逻辑表对应的真实表到sharding上下文中
                this.loadTableActualNodes(entry.getKey(), entry.getValue());
            }
        }
    }

    private void loadTableActualNodes(String logicTableName, NodeNameMatchingFunction matching) {
        ShardingRule shardingRule = getShardingRule();
        if (shardingRule == null) {
            // 如果分表规则为空, 说明没有配置分表规则，因此不需要加载分表的真实节点列表. 直接return
            log.warn("Not found sharding rule, logic table name {}.", logicTableName);
            return;
        }
        Map<String, String> namesMap = shardingRule.getMasterSlaveRules().stream().collect(Collectors.toMap(MasterSlaveRule::getName, MasterSlaveRule::getMasterDataSourceName));
        // 分表规则
        TableRule tableRule = shardingRule.getTableRule(logicTableName);
        // 真实数据库节点.
        Collection<String> actualDatasourceNames = tableRule.getActualDatasourceNames();
        for (String datasourceName : actualDatasourceNames) {
            // shardingsphere上下文中加载的所有真实节点数据表.
            Collection<String> actualTableNames = tableRule.getActualTableNames(datasourceName);
            // 获取真实的表名
            Set<String> actualNodes = getActualNodes(logicTableName, matching, namesMap, datasourceName);
            if ((CollectionUtils.isEmpty(actualNodes) && CommonSwitcher.ENABLE_SUING_LOGIC_TABLE_WHEN_ACTUAL_NODES_EMPTY.isOff()) ||
                    !actualTableNames.containsAll(actualNodes)) {
                // 刷新所有节点表
                reloadDatasourceActualTables(tableRule, datasourceName, actualNodes);
            }
        }
    }


    @Override
    public List<String> selectAllTableNameByDb(String dbName) {
        return this.actualTableNames.get(dbName).stream().toList();
    }

    @Override
    public CreateTableSql selectTableCreateSql(String tableName) {
        AssertUtil.notEmpty(tableName, "Table name should not be empty.");
        JdbcTemplate template = this.shardingJdbcTemplates.get(dataSourceName);
        Map<String, Object> map = template.queryForMap(SqlConstants.getSelectCreateTableDdl(tableName));
        if (MapUtils.isNotEmpty(map)) {
            String table = (String) map.get("Table");
            String createTable = (String) map.get("Create Table");
            return new CreateTableSql(table, createTable);
        }
        return null;
    }

    @Override
    public void execute(final String sql) {
        this.shardingJdbcTemplates.values().forEach(jdbcTemplate -> jdbcTemplate.execute(sql));
    }
}
