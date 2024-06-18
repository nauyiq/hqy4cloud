package com.hqy.cloud.sharding.algorithm;

import cn.hutool.extra.spring.SpringUtil;
import com.hqy.cloud.sharding.service.ShardingService;
import com.hqy.cloud.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Map;
import java.util.Set;

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
public abstract class ShardingTableAlgorithmTool<T extends Comparable<?>> implements StandardShardingAlgorithm<T> {


    /**
     * 检查是否存在表名，如果存在直接返回该表名。不存在则创建该真实表
     * @param logicTableName  逻辑表名
     * @param actualTableName 真实表名
     * @return                真实表名
     */
    public String checkAndCreateShardingTableName(String logicTableName, String actualTableName) {
        AssertUtil.notEmpty(logicTableName, "Logic table name should not be empty.");
        AssertUtil.notEmpty(actualTableName, "Actual table name should not be empty.");
        // 存在则直接返回
        if (checkShardingTableExist(actualTableName)) {
            return actualTableName;
        }
        synchronized (logicTableName.intern()) {
            if (!checkShardingTableExist(actualTableName)) {
                ShardingService shardingService = SpringUtil.getBean(ShardingService.class);
                shardingService.addTableActualNode(logicTableName, actualTableName);
            }
        }
        return actualTableName;
    }


    protected boolean checkShardingTableExist(String actualTableName) {
        ShardingService shardingService = SpringUtil.getBean(ShardingService.class);
        Map<String, Set<String>> allTables = shardingService.getAllTables();
        return allTables.values().stream().anyMatch(set -> set.contains(actualTableName));
    }


    /**
     * 获取当前分表的逻辑表名
     * @return 逻辑表名
     */
    protected abstract String getLogicTableName();


}
