package com.hqy.cloud.shardingsphere.algorithm;

import com.hqy.cloud.db.common.CreateTableSql;
import com.hqy.cloud.db.mapper.CommonMapper;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.underlying.common.exception.ShardingSphereException;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 分表算法工具, 提供自动建表的能力, 用于数据归档等需要自动建表的情况 </br>
 * 前提: 1. 由子类指定默认的数据库名, 该类会在初始化时加载该库下的所有表到缓存中
 *      2. 数据库用户有访问information_schema库的权限
 *      3. 真实存在某个实际分表的逻辑表, 因为会根据逻辑表DDL创建需要自动创建的表
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/31
 */
@Slf4j
public abstract class ShardingTableAlgorithmTool<T extends Comparable<?>> implements PreciseShardingAlgorithm<T>, RangeShardingAlgorithm<T> {

    private CommonMapper commonMapper;
    private volatile boolean init = false;
    private final Set<String> tableNames = new CopyOnWriteArraySet<>();


    /**
     * 检查是否存在表名，如果存在直接返回该表名。不存在则创建该真实表
     * @param logicTableName  逻辑表名
     * @param actualTableName 真实表名
     * @return                真实表名
     */
    public String checkAndCreateShardingTableName(String logicTableName, String actualTableName) {
        AssertUtil.notEmpty(logicTableName, "Logic table name should not be empty.");
        AssertUtil.notEmpty(actualTableName, "Actual table name should not be empty.");
        // 是否初始化了
        if (!init) {
            // 初始化.
            doInitialize();
        }
        // 存在则直接返回
        if (checkShardingTableExist(actualTableName)) {
            return actualTableName;
        }
        // 不存在则创建表.
        // 搜索当前逻辑表的创建表语句
        synchronized (logicTableName.intern()) {
            if (!checkShardingTableExist(actualTableName)) {
                CreateTableSql tableSql = commonMapper.selectTableCreateSql(logicTableName);
                if (tableSql == null) {
                    throw new ShardingSphereException("Failed execute to auto create table: "
                            + actualTableName + ", because not found " + logicTableName + " ddl");
                }
                // 创建表
                String createTable = tableSql.getCreateTable();
                createTable = createTable.replace("CREATE TABLE", "CREATE TABLE IF NOT EXISTS")
                                         .replace(logicTableName, actualTableName);
                commonMapper.executeSql(createTable);
                tableNames.add(actualTableName);
            }
        }
        return actualTableName;
    }

    protected boolean checkShardingTableExist(String actualTableName) {
        return tableNames.contains(actualTableName);
    }


    private synchronized void doInitialize() {
        String defaultDbName = getDefaultDbName();
        AssertUtil.notEmpty(defaultDbName, "Default db name should not be empty.");
        // 从容器中获取commentMapper
        this.commonMapper = SpringContextHolder.getBean(CommonMapper.class);
        AssertUtil.notNull(this.commonMapper, "Common mapper is null from spring context.");
        // 加载所有的数据表到缓存中.
        loadTableNamesByScheme(defaultDbName);
        this.init = true;
    }


    public void loadTableNamesByScheme(String scheme) {
        List<String> allTableNameBySchema = commonMapper.getAllTableNameBySchema(scheme);
        log.info("Load {} tables: {}.", scheme, allTableNameBySchema);
        this.tableNames.addAll(allTableNameBySchema);
    }


    /**
     * 获取默认数据库的名字
     * @return 数据库名, 查询对应逻辑表的建表sql
     */
    protected abstract String getDefaultDbName();



}
