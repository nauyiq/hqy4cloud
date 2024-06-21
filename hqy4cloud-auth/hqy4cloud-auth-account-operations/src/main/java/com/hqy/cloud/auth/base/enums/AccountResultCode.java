package com.hqy.cloud.auth.base.enums;

import com.hqy.cloud.common.result.Result;

/**
 * 用户相关操作业务状态码定义
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/21
 */
public enum AccountResultCode implements Result {

    /**
     * 找不到该角色
     */
    NOT_FOUND_ROLE(3006, "Not found role, please check your input."),


    ;

    public final int code;

    public final String message;

    AccountResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "";
    }

    @Override
    public int getCode() {
        return 0;
    }
}
