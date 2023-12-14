package com.hqy.cloud.datasource.core;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/8 16:49
 */
public enum SqlExceptionType {

    /**
     * 慢sql
     */
    SLOW(1),

    /**
     * 异常sql
     */
    ERROR(2)


    ;

    public final int value;

    SqlExceptionType(int value) {
        this.value = value;
    }
}
