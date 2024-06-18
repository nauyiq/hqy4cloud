package com.hqy.cloud.db.common;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/8
 */
public enum BusinessDbCode {


    ;

    public final int code;

    public final int tableCount;

    BusinessDbCode(int code, int tableCount) {
        this.code = code;
        this.tableCount = tableCount;
    }
}
