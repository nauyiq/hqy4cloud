package com.hqy.cloud.db.service;

import com.hqy.cloud.db.common.CreateTableSql;

import java.util.List;

/**
 * 提供通用的db能力.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/4
 */
public interface CommonDbService {

    /**
     * 查询某个数据中所有的表名
     * @param db 数据库名
     * @return   数据库中所有的表名
     */
    List<String> selectAllTableNameByDb(String db);

    /**
     * 查询某张表的建表DDL
     * @param tableName 表名
     * @return          {@link CreateTableSql}
     */
    CreateTableSql selectTableCreateSql(String tableName);

    /**
     * 执行sql
     * @param sql
     */
    void execute(String sql);


}
