package com.hqy.cloud.canal.model;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:18
 */
public interface ModelTable {

    /**
     * 数据库名
     * @return database name
     */
    String database();

    /**
     * 表明
     * @return table name
     */
    String table();

    /**
     * create ModelTable
     * @param database database name
     * @param table    table name
     * @return         this.
     */
    static ModelTable of(String database, String table) {
        return DefaultModelTable.of(database, table);
    }
}
