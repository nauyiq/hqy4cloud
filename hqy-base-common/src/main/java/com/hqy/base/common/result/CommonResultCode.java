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
    SYSTEM_ERROR_INSERT_FAIL(9100, "System is busy, insert data failure."),

    /**
     * 更新数据异常.
     */
    SYSTEM_ERROR_UPDATE_FAIL(9200, "System is busy, update data failure."),

    /**
     * 无效的token
     */
    INVALID_ACCESS_TOKEN(9300, "Invalid token, token expired or invalid."),

    /**
     * 权限不够
     */
    LIMITED_AUTHORITY(9400, "Access authority Limit."),

    /**
     * 耗时的rpc方法
     */
    CONSUMING_TIME_RPC(9500, "Consuming time RPC method"),


    /**
     * 错误参数
     */
    ERROR_PARAM(1001, "Invalid parameter, please check parameter again."),

    /**
     * 错误参数，检查参数是否存在
     */
    ERROR_PARAM_UNDEFINED(1002, "Invalid parameter, please check undefined."),


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
     * 无效的邮箱
     */
    INVALID_EMAIL(2004, "Please input valid email."),

    /**
     * 邮箱已经存在.
     */
    EMAIL_EXIST(2005, "Account email already exist."),

    /**
     * 验证码错误
     */
    VERIFY_CODE_ERROR(2006, "Verify code error, please input right code."),


    /**
     * 错误的用户名或者密码
     */
    INVALID_ACCESS_USER(3001, "Username or password incorrect!"),

    /**
     * 密码错误
     */
    PASSWORD_ERROR(3002, "Please input the correct password."),


    /**
     * 上传文件失败.
     */
    INVALID_UPLOAD_FILE(10001, "Failed execute to upload file."),

    /**
     * 上传文件失败, 文件类型不支持
     */
    INVALID_FILE_TYPE(10002, "Failed execute to upload file, file type not supported."),


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

    public static MessageResponse messageResponse(int code, String message) {
        return new MessageResponse(false, message, code);
    }


    public static DataResponse dataResponse() {
        return dataResponse(true, SUCCESS, null);
    }

    public static DataResponse dataResponse(Object data) {
        return dataResponse(CommonResultCode.SUCCESS, data);
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

    public static DataResponse dataResponse(int code, String message) {
        return new DataResponse(false, message, code, null);
    }


}
