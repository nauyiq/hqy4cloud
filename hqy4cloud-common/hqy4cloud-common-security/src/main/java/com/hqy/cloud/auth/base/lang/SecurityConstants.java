package com.hqy.cloud.auth.base.lang;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24 15:37
 */
public interface SecurityConstants {

    /**
     * 角色前缀
     */
    String ROLE = "ROLE_";

    /**
     * 前缀
     */
    String PROJECT_PREFIX = "hongqy";

    /**
     * 项目的license
     */
    String PROJECT_LICENSE = "https://admin.hongqy1024.com";

    /**
     * 内部
     */
    String FROM_IN = "Y";

    /**
     * 标志
     */
    String FROM = "from";

    /**
     * 请求header
     */
    String HEADER_FROM_IN = FROM + "=" + FROM_IN;

    /**
     * 默认登录URL
     */
    String OAUTH_TOKEN_URL = "/oauth2/token";

    /**
     * grant_type
     */
    String REFRESH_TOKEN = "refresh_token";

    /**
     * 邮箱号登录
     */
    String EMAIL = "email";

    /**
     * {bcrypt} 加密的特征码
     */
    String BCRYPT = "{bcrypt}";

    /**
     * {noop} 加密的特征码
     */
    String NOOP = "{noop}";

    /***
     * 资源服务器默认bean名称
     */
    String RESOURCE_SERVER_CONFIGURER = "resourceServerConfigurerAdapter";

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
     * 协议字段
     */
    String DETAILS_LICENSE = "license";

    /**
     * 验证码有效期,默认 60秒
     */
    long CODE_TIME = 60;

    /**
     * 验证码长度
     */
    String CODE_SIZE = "6";

    /**
     * 客户端模式
     */
    String CLIENT_CREDENTIALS = "client_credentials";

    /**
     * 客户端ID
     */
    String CLIENT_ID = "clientId";

    /**
     * 邮箱登录 参数名称
     */
    String EMAIL_PARAMETER_NAME = "email";

    /**
     * 验证码code 参数名称
     */
    String CODE_PARAMETER_NAME = "code";

    /**
     * 无效的验证码
     */
    String INVALID_REQUEST_CODE = "invalid_request_code";


    /**
     * 授权码模式confirm
     */
    String CUSTOM_CONSENT_PAGE_URI = "/auth/confirm";

}
