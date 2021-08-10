package com.hqy.common.result;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-09 19:33
 */
public enum CommonResult {

    SUCCESS(0, "OK."),

    SYSTEM_ERROR(9999, "System internal error, Please try again later."),

    ERROR_PARAM(1001, "invalid parameter, Internal error"),
    ;

    public int code;

    public String message;

    CommonResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
