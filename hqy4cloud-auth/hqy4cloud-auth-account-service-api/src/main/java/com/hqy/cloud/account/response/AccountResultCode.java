package com.hqy.cloud.account.response;

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
    USER_NOT_FOUND("A2000", "User not found."),


    /**
     * 当前用户是禁用状态
     */
    USER_DISABLED("A2001", "The user disabled."),

    /**
     * 用户名不能为空
     */
    USERNAME_EMPTY("A2002", "The username cannot be empty."),

    /**
     * 用户名已经存在
     */
    USERNAME_EXIST("A2003","This username already exist."),

    /**
     * 无效的邮箱
     */
    INVALID_EMAIL("A2004", "Please input valid email."),

    /**
     * 邮箱已经存在.
     */
    EMAIL_EXIST("A2005", "Account email already exist."),

    /**
     * 找不到邮箱
     */
    NOT_FOUND_EMAIL("A2006", "Email not found."),

    /**
     * 电话已经存在
     */
    PHONE_EXIST("A2007","This phone already exist."),

    /**
     * 验证码错误
     */
    VERIFY_CODE_ERROR("A2008", "Verify code error, please input right code."),


    /**
     * 用户已经存在
     */
    USER_EXIST("A2009", "This user already exist."),

    /**
     * 注册用户失败
     */
    REGISTER_ACCOUNT_FAILED("A2010", "Failed execute to register account."),

    /**
     * 密码错误
     */
    PASSWORD_ERROR("A2011", "Please input the correct password."),

    /**
     * 用户实名认证失败
     */
    USER_AUTH_FAIL("A2012", "用户实名认证失败"),

    /**
     * 租户id或租户秘钥错误
     */
    INVALID_CLIENT_OR_SECRET("A2022", "ClientId or secret incorrect."),



    ;

    public final String code;

    public final String message;

}
