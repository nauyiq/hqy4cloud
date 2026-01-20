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

    USER_NOT_FOUND("USER_NOT_FOUND", "找不到该用户"),
    USER_DISABLED("USER_DISABLED", "当前用户已被禁用"),

    USERNAME_EMPTY("USERNAME_EMPTY", "用户名不能为空"),
    USERNAME_EXIST("USERNAME_EXIST","用户名已经存在"),

    INVALID_EMAIL("INVALID_EMAIL", "无效的邮箱"),

    EMAIL_EXIST("EMAIL_EXIST", "邮箱已经存在"),

    NOT_FOUND_EMAIL("NOT_FOUND_EMAIL", "找不到邮箱"),

    PHONE_EXIST("PHONE_EXIST","电话已经存在"),

    VERIFY_CODE_ERROR("VERIFY_CODE_ERROR", "验证码错误"),

    USER_EXIST("USER_EXIST", "用户已经存在"),

    REGISTER_ACCOUNT_FAILED("REGISTER_ACCOUNT_FAILED", "注册账号失败"),

    PASSWORD_ERROR("PASSWORD_ERROR", "密码错误"),

    USER_AUTH_FAIL("USER_AUTH_FAIL", "用户实名认证失败"),

    AUTH_CLIENT_NOT_EXIST("AUTH_CLIENT_NOT_EXIST", "租户不存在"),

    INVALID_CLIENT_OR_SECRET("INVALID_CLIENT_OR_SECRET", "租户id或租户秘钥错误"),

    UNSUPPORTED_AUTHENTICATION_GRANT_TYPE("UNSUPPORTED_AUTHENTICATION_GRANT_TYPE", "不支持的授权类型"),

    UNSUPPORTED_AUTHENTICATION_GRANT_SCOPE("UNSUPPORTED_AUTHENTICATION_GRANT_SCOPE", "不支持的授权范围"),


    ;

    public final String code;

    public final String message;

}
