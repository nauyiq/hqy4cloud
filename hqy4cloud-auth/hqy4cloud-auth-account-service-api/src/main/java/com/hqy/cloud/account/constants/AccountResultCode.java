package com.hqy.cloud.account.constants;

import com.hqy.cloud.common.result.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户相关操作业务状态码定义
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/21
 */
@Getter
@AllArgsConstructor
public enum AccountResultCode implements Result {

    /**
     * 找不到该用户
     */
    USER_NOT_FOUND("USER_NOT_FOUND", "找不到该用户"),


    /**
     * 当前用户是禁用状态
     */
    USER_DISABLED("USER_DISABLED", "当前用户已被禁用"),

    /**
     * 用户名不能为空
     */
    USERNAME_EMPTY("USERNAME_EMPTY", "用户名不能为空"),

    /**
     * 用户名已经存在
     */
    USERNAME_EXIST("USERNAME_EXIST","用户名已经存在"),

    /**
     * 无效的邮箱
     */
    INVALID_EMAIL("INVALID_EMAIL", "无效的邮箱"),

    /**
     * 邮箱已经存在.
     */
    EMAIL_EXIST("EMAIL_EXIST", "邮箱已经存在"),

    /**
     * 找不到邮箱
     */
    NOT_FOUND_EMAIL("NOT_FOUND_EMAIL", "找不到邮箱"),

    /**
     * 电话已经存在
     */
    PHONE_EXIST("PHONE_EXIST","电话已经存在"),

    /**
     * 验证码错误
     */
    VERIFY_CODE_ERROR("VERIFY_CODE_ERROR", "验证码错误"),


    /**
     * 用户已经存在
     */
    USER_EXIST("USER_EXIST", "用户已经存在"),

    /**
     * 注册用户失败
     */
    REGISTER_ACCOUNT_FAILED("A2010", "Failed execute to register account."),

    /**
     * 密码错误
     */
    PASSWORD_ERROR("PASSWORD_ERROR", "密码错误"),

    /**
     * 用户实名认证失败
     */
    USER_AUTH_FAIL("USER_AUTH_FAIL", "用户实名认证失败"),

    /**
     * 租户id不存在
     */
    AUTH_CLIENT_NOT_EXIST("AUTH_CLIENT_NOT_EXIST", "租户不存在"),

    /**
     * 租户id或租户秘钥错误
     */
    INVALID_CLIENT_OR_SECRET("A2022", "ClientId or secret incorrect."),


    ;

    public final String code;

    public final String message;

}
