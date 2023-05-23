package com.hqy.cloud.canal.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:14
 */
@Getter
@RequiredArgsConstructor
public enum OperationType {

    /**
     * DML
     */
    DML("dml", "DML语句"),

    /**
     * DDL
     */
    DDL("ddl", "DDL语句"),

    ;

    private final String type;
    private final String description;

}
