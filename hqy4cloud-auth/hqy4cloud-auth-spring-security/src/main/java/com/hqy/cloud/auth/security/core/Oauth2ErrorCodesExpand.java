package com.hqy.cloud.auth.security.core;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24 15:23
 */
public interface Oauth2ErrorCodesExpand {

    /** 用户名未找到 */
    String USERNAME_NOT_FOUND = "username_not_found";

    /** 错误凭证 */
    String BAD_CREDENTIALS = "bad_credentials";

    /** 用户被锁 */
    String USER_LOCKED = "user_locked";

    /** 用户禁用 */
    String USER_DISABLE = "user_disable";

    /** 用户过期 */
    String USER_EXPIRED = "user_expired";

    /** 证书过期 */
    String CREDENTIALS_EXPIRED = "credentials_expired";

    /** scope 为空异常 */
    String SCOPE_IS_EMPTY = "scope_is_empty";

    /**
     * 令牌不存在
     */
    String TOKEN_MISSING = "token_missing";

    /** 未知的登录异常 */
    String UN_KNOW_LOGIN_ERROR = "un_know_login_error";

    /**
     * 不合法的Token
     */
    String INVALID_BEARER_TOKEN = "invalid_bearer_token";

}
