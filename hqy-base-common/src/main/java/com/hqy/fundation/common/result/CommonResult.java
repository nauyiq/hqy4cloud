package com.hqy.fundation.common.result;

/**
 * 全局错误码和消息提示
 * @author qy
 * @date 2021-08-09 19:33
 */
public enum CommonResult {

    /**
     * 成功调用
     */
    SUCCESS(0, "OK."),

    /**
     * 系统异常
     */
    SYSTEM_ERROR(9999, "System internal error, Please try again later."),

    /**
     * 错误参数
     */
    ERROR_PARAM(1001, "invalid parameter, please check parameter again."),
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
