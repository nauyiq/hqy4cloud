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


    //  ==================== 参数异常 ====================
    INCORRECT_PASSWORD("101001", "错误的用户名或者密码"),
    VERIFY_CODE_ERROR("101002", "验证码错误"),
    UNSUPPORTED_AUTHENTICATION_GRANT_TYPE("101003", "不支持的授权类型"),
    UNSUPPORTED_AUTHENTICATION_GRANT_SCOPE("101004", "不支持的授权范围"),

    // ==================== 数据异常 ====================
    ACCOUNT_NOT_FOUND("201001", "账户不存在"),
    ACCOUNT_ALREADY_CANCELLED("201002", "账号已注销, 请联系客服"),
    USER_DISABLED("201003", "当前用户已被禁用"),
    AUTH_CLIENT_NOT_EXIST("201004", "租户不存在"),
    INVALID_CLIENT_OR_SECRET("201005", "租户id或租户秘钥错误"),

    // ==================== 业务异常 ====================
    REGISTER_ACCOUNT_FAILED("301001", "注册账号失败"),
    USER_AUTH_FAIL("301002", "用户实名认证失败"),




    ;

    public final String code;

    public final String message;

}
