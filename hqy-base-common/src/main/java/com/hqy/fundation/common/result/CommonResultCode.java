package com.hqy.fundation.common.result;

/**
 * 全局错误码和消息提示
 * @author qy
 * @date 2021-08-09 19:33
 */
public enum CommonResultCode {

    /**
     * 成功调用
     */
    SUCCESS(0, "OK."),

    /**
     * 系统异常
     */
    SYSTEM_ERROR(9999, "System internal error, Please try again later."),

    /**
     * 系统繁忙
     */
    SYSTEM_BUSY(9000, "System is busy, Please try again later."),


    /**
     * 异常请求 封禁几分钟
     */
    ILLEGAL_REQUEST_LIMITED(9001, "Illegal Request, Limit a few minutes."),


    /**
     * 新增数据异常
     */
    SYSTEM_ERROR_INSERT_FAIL(9100, "System internal error, insert data failure, please try again later."),

    /**
     * 无效的token
     */
    INVALID_ACCESS_TOKEN(9200, "Invalid token, token expired or invalid."),

    /**
     * 权限不够
     */
    LIMITED_AUTHORITY(9300, "Access authority Limit."),

    /**
     * 耗时的rpc方法
     */
    CONSUMING_TIME_RPC(9400, "Consuming time RPC method"),

    /**
     * 无效的数据
     */
    INVALID_DATA(1003, "System internal error, invalid data, please check inputData again"),

    /**
     * 错误参数
     */
    ERROR_PARAM(1001, "invalid parameter, please check parameter again."),


    /**
     * 找不到该用户
     */
    USER_NOT_FOUND(2000, "User not found."),

    /**
     * 当前用户是禁用状态
     */
    USER_DISABLED(2001, "The user disabled.")

    ;

    public int code;

    public String message;

    CommonResultCode(int code, String message) {
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
