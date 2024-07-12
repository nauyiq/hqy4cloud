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
     * 找不到邮箱
     */
    NOT_FOUND_EMAIL(2006, "Email not found."),

    /**
     * 电话已经存在
     */
    PHONE_EXIST(2007,"This phone already exist."),

    /**
     * 验证码错误
     */
    VERIFY_CODE_ERROR(2008, "Verify code error, please input right code."),


    /**
     * 用户已经存在
     */
    USER_EXIST(2009, "This user already exist."),

    /**
     * 注册用户失败
     */
    REGISTER_ACCOUNT_FAILED(2010, "Failed execute to register account."),

    /**
     * 密码错误
     */
    PASSWORD_ERROR(2011, "Please input the correct password."),

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
        return this.message;
    }

    @Override
    public int getCode() {
        return this.code;
    }
}
