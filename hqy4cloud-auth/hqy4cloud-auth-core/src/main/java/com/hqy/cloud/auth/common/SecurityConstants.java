package com.hqy.cloud.auth.common;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24 15:37
 */
public interface SecurityConstants {

    /**
     * 邮箱号登录
     */
    String EMAIL = "email";

    /**
     * 手机验证码登录
     */
    String SMS = "sms";

    /**
     * 密码模式登录
     */
    String PASSWORD = "password";

    /**
     * 验证码code 参数名称
     */
    String CODE_PARAMETER_NAME = "code";

    /**
     * 手机号码验证code， 参数名称
     */
    String PHONE_PARAMETER_NAME = "phone";

    /**
     * 用户名
     */
    String USERNAME = "username";

    /**
     * id
     */
    String ID = "id";

    /**
     * 角色
     */
    String ROLES = "roles";


    /**
     * 客户端模式
     */
    String CLIENT_CREDENTIALS = "client_credentials";

    /**
     * 客户端ID
     */
    String CLIENT_ID = "clientId";

    /**
     * 授权码模式confirm
     */
    String CUSTOM_CONSENT_PAGE_URI = "/auth/confirm";

    /**
     * 所有授权模式
     */
    String ALL_GRANT_SCOPE = "all";
}
