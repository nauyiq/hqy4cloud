package com.hqy.base.common.result;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.bind.MessageResponse;

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
    SYSTEM_ERROR(9999, "System internal error, please try again later."),

    /**
     * 系统繁忙
     */
    SYSTEM_BUSY(9000, "System is busy, please try again later."),


    /**
     * 异常请求 封禁几分钟
     */
    ILLEGAL_REQUEST_LIMITED(9001, "Illegal Request, Limit a few minutes."),

    /**
     * 接口限流了
     */
    INTERFACE_LIMITED(9002, "Interface limited, please try again later."),


    /**
     * 新增数据异常
     */
    SYSTEM_ERROR_INSERT_FAIL(9100, "Insert data to db failure."),

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
     * 错误参数
     */
    ERROR_PARAM(1001, "Invalid parameter, please check parameter again."),


    /**
     * 无效的数据
     */
    INVALID_DATA(1003, "Invalid data, please check input again"),


    /**
     * 找不到该用户
     */
    USER_NOT_FOUND(2000, "User not found."),

    /**
     * 当前用户是禁用状态
     */
    USER_DISABLED(2001, "The user disabled."),

    /**
     * 用户名不能为空
     */
    USERNAME_EMPTY(2002, "The username cannot be empty."),

    /**
     * 用户名已经存在
     */
    USERNAME_EXIST(2003,"This username already exist."),

    /**
     * 错误的用户名或者密码
     */
    INVALID_ACCESS_USER(3001, "Username or password incorrect!"),

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


    public static MessageResponse messageResponse(){
        return messageResponse(true, SUCCESS);
    }

    public static MessageResponse messageResponse(CommonResultCode code) {
        return messageResponse(false, code);
    }

    public static MessageResponse messageResponse(boolean result, CommonResultCode code) {
        return new MessageResponse(result, code.message, code.code);
    }


    public static DataResponse dataResponse() {
        return dataResponse(true, SUCCESS, null);
    }

    public static DataResponse dataResponse(CommonResultCode code) {
        return dataResponse(false, code, null);
    }

    public static DataResponse dataResponse(CommonResultCode code, Object data) {
        return dataResponse(true, code, data);
    }

    public static DataResponse dataResponse(boolean result, CommonResultCode code, Object data) {
        return new DataResponse(result, code.message, code.code, data);
    }


}
