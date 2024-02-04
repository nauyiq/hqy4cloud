package com.hqy.cloud.db.common;

import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/31
 */
public class CreateTableSql implements Serializable {

    @Column(name = "Table")
    private String table;
    @Column(name = "Create Table")
    private String createTable;

    public CreateTableSql() {
    }

    public CreateTableSql(String table, String createTable) {
        this.table = table;
        this.createTable = createTable;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getCreateTable() {
        return createTable;
    }

    public void setCreateTable(String createTable) {
        this.createTable = createTable;
    }
}
