package com.hqy.cloud.canal.model;

import com.hqy.cloud.canal.common.BinLogEventType;
import com.hqy.cloud.canal.common.OperationType;
import lombok.Data;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:17
 */
@Data
public class CanalBinLogResult<T> {

    /**
     * 提取的长整型主键
     */
    private Long primaryKey;


    /**
     * binlog事件类型
     */
    private BinLogEventType binLogEventType;

    /**
     * 更变前的数据
     */
    private T beforeData;

    /**
     * 更变后的数据
     */
    private T afterData;

    /**
     * 数据库名称
     */
    private String databaseName;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * sql语句 - 一般是DDL的时候有用
     */
    private String sql;

    /**
     * mysql操作类型
     */
    private OperationType operationType;
}
