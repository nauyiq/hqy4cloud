package com.hqy.cloud.db.common;

import cn.hutool.core.text.CharPool;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/5
 */
public class SqlConstants {

    /**
     * 查询某个数据库下所有的表名集合
     */
    public static final String SELECT_ALL_TABLE_NAME_BY_DB_NAME
            = "SELECT TABLES.TABLE_NAME FROM information_schema.TABLES WHERE TABLES.TABLE_SCHEMA = ";

    /**
     * 查询某张表的建表DDL
     */
    public static final String SELECT_CREATE_TABLE_DDL = "SHOW CREATE TABLE ";

    /**
     * 查询当前正在使用的数据库名称
     */
    public static final String GET_USING_DATABASE_NAME = "SELECT DATABASE()";


    public static String getSelectAllTableNameByDbName(String dbName) {
        dbName = CharPool.SINGLE_QUOTE + dbName + CharPool.SINGLE_QUOTE;
        return SELECT_ALL_TABLE_NAME_BY_DB_NAME + dbName;
    }

    public static String getSelectCreateTableDdl(String tableName) {
        return SELECT_CREATE_TABLE_DDL + tableName;
    }







}
