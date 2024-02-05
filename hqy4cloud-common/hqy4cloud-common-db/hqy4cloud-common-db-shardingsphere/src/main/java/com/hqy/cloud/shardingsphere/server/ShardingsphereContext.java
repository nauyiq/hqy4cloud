package com.hqy.cloud.shardingsphere.server;

import com.hqy.cloud.db.service.CommonDbService;
import com.hqy.cloud.shardingsphere.algorithm.NodeNameMatchingFunction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * shardingsphere上下文.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/4
 */
public interface ShardingsphereContext {

    /**
     * 需要初始化的表名字
     */
    Map<String, NodeNameMatchingFunction> INITIALIZE_TABLES = new ConcurrentHashMap<>();

    /**
     * 加载某张表的真实列表, 默认加载以 logicTableName 开头的所有的表
     * @param logicTableName 逻辑表名
     */
    void loadTableActualNodes(String logicTableName);

    /**
     * 加载某张表的真实列表, 真实表名会通过匹配函数进行匹配
     * @param logicTableName 逻辑表明
     * @param matching       表名检验函数
     */
    void loadTableActualNodes(String logicTableName, NodeNameMatchingFunction matching);

    /**
     * 添加某张表的真实节点
     * @param logicTableName 逻辑表名
     * @param actualNode     真实节点
     */
    void addTableActualNode(String logicTableName, String actualNode);

    /**
     * 获取sharding-jdbc上下文
     * @return {@link ShardingJdbcContext}
     */
    ShardingJdbcContext getJdbcContext();

    /**
     * 获取通用Db service
     * @return {@link CommonDbService}
     */
    CommonDbService getCommonDbService();




}
