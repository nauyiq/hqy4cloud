package com.hqy.cloud.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接口级别错误码
 * @author qy
 * @date 2021-08-09
 */
@Getter
@AllArgsConstructor
public enum ResultCode implements Result {

    /**
     * 成功调用
     */
    SUCCESS("0000", "Success"),

    /**
     * 失败
     */
    FAILED("9999", "Failed."),

    /**
     * 系统繁忙
     */
    SYSTEM_BUSY("9999", "System is busy, please try again later."),

    /**
     * 系统异常
     */
    SYSTEM_ERROR("9999", "System internal error, please try again later."),

    /**
     * 异常请求 封禁几分钟
     */
    ILLEGAL_REQUEST_LIMITED("9001", "Illegal Request, Limit a few minutes."),

    /**
     * 接口限流了
     */
    INTERFACE_LIMITED("9002", "Too many request, please try again later."),

    /**
     * rpc请求太多
     */
    RPC_INTERFACE_TOO_MANY_REQUEST("9003", "Too many request for interval, please try again later."),

    /**
     * 接口已被降级
     */
    INTERFACE_ALREADY_DEGRADE("9004", "The api already degrade, please try again later."),

    /**
     * 接口热点参数限流
     */
    INTERFACE_PRAM_HOT_KET_LIMIT("9005", "The api of hotkey limited, please try again later."),

    /**
     * 授权规则不足被SENTINEL限流
     */
    SENTINEL_LIMITED_AUTHORITY("9006", "The request access authority Limit by monitor."),

    /**
     * 新增数据异常
     */
    SYSTEM_ERROR_INSERT_FAIL("9100", "System is busy, insert data failure."),

    /**
     * 更新数据异常.
     */
    SYSTEM_ERROR_UPDATE_FAIL("9200", "System is busy, update data failure."),

    /**
     * 无效的token
     */
    INVALID_ACCESS_TOKEN("INVALID_ACCESS_TOKEN", "令牌不存在或已过期."),

    /**
     * 权限不够
     */
    LIMITED_AUTHORITY("9400", "Access authority Limit."),

    /**
     * 耗时的rpc方法
     */
    CONSUMING_TIME_RPC("9500", "Consuming time RPC method"),

    /**
     * 无效的认证
     */
    INVALID_AUTHORIZATION("9600", "Invalid authorization, please check your authorization again."),

    /**
     * 没有权限
     */
    NOT_PERMISSION("9700", "Not permission."),

    /**
     * 没有登录
     */
    NOT_LOGIN("9800", "Please login first."),

    /**
     * 错误参数
     */
    ERROR_PARAM("1001", "Invalid parameter, please check parameter again."),

    /**
     * 错误参数，检查参数是否存在
     */
    ERROR_PARAM_UNDEFINED("ERROR_PARAM_UNDEFINED", "参数为空，请检查"),

    /**
     * 无效的数据
     */
    INVALID_DATA("1003", "Invalid data, please check input again"),

    /**
     * 当前环境未启用/支持短信发送
     */
    NOT_SUPPORT_SMS("2000", "The environment not support sms."),

    /**
     * 找不到短信模板
     */
    NOT_FOUND_SMS_TEMPLATE("NOT_FOUND_SMS_TEMPLATE", "未找到短信模板"),

    /**
     * 发送短信失败
     */
    FAILED_SEND_SMS("2009", "Failed execute to send sms message."),


    /**
     * 错误的用户名或者密码
     */
    INVALID_ACCESS_USER("3001", "Username or password incorrect."),



    /**
     * 权限不够，无法设置的账户权限
     */
    LIMITED_SETTING_ROLE_LEVEL("3004", "Access authority limit to modify role level."),

    /**
     * 角色名已经存在
     */
    ROLE_NAME_EXIST("3005", "This role name already exist."),


    /**
     * 空数据
     */
    DATA_EMPTY("4000", "Data is empty."),

    /**
     * 获取不到Menu
     */
    NOT_FOUND_MENU("5001", "Not found menu, please check you input again."),

    /**
     * 无效的菜单类型
     */
    INVALID_MENU_TYPE("5002", "Invalid menu type, please check your input again."),

    /**
     * 获取不到资源
     */
    NOT_FOUND_RESOURCE("5002", "Not found resource, please check you input again."),




    /**
     * 上传文件失败.
     */
    INVALID_UPLOAD_FILE("10001", "Failed execute to upload file."),

    /**
     * 上传文件失败, 文件类型不支持
     */
    INVALID_FILE_TYPE("10002", "Failed execute to upload file, file type not supported."),

    ;

    public final String code;

    public final String message;


}
