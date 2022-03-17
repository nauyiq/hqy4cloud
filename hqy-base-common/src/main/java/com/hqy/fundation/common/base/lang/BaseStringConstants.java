package com.hqy.fundation.common.base.lang;

/**
 * 全局常量类
 * @author qiyuan.hong
 * @date 2022-02-15 23:23
 */
public interface BaseStringConstants {

    /**
     * 表示websocket 连接的id
     */
    String UID = "uid";

    /**
     * 字符串unknown
     */
    String UNKNOWN = "unknown";

    /**
     * application/json;charset=UTF-8
     */
    String APPLICATION_JSON_UTF_8 = "application/json;charset=UTF-8";

    /**
     * websocket
     */
    String WEBSOCKET = "websocket";

    interface Auth {
        /**
         * 认证请求头key
         */
        String AUTHORIZATION_KEY = "Authorization";

        /**
         * JWT令牌前缀
         */
        String JWT_PREFIX = "Bearer ";

        /**
         * Basic认证前缀
         */
        String BASIC_PREFIX = "Basic ";

        /**
         * JWT载体key
         */
        String JWT_PAYLOAD_KEY = "payload";

        /**
         * JWT ID 唯一标识
         */
        String JWT_JTI = "jti";

        /**
         * JWT ID 唯一标识
         */
        String JWT_EXP = "exp";

        /**
         * JWT存储权限前缀
         */
        String AUTHORITY_PREFIX = "ROLE_";

        /**
         * JWT存储权限属性
         */
        String JWT_AUTHORITIES_KEY = "authorities";

        /**
         * refresh_token
         */
        String REFRESH_TOKEN_KEY = "refresh_token";

        /**
         * 认证身份标识
         */
        String AUTHENTICATION_IDENTITY_KEY = "authenticationIdentity";
    }



    interface Headers {
        /**
         * 请求头Content-Type字符串
         */
        String CONTENT_TYPE = "Content-Type";

        /**
         * 请求头 x-forwarded-for
         */
        String X_FORWARDED_FOR = "x-forwarded-for";

        /**
         * 请求头 proxy-Client-IP
         */
        String PROXY_CLIENT_IP = "Proxy-Client-IP";

        /**
         * 请求头 WL-Proxy-Client-IP
         */
        String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";

        /**
         * Upgrade
         */
        String UPGRADE = "Upgrade";

        /**
         * token
         */
        String TOKEN = "token";
    }


    interface Symbol {

        /**
         * 英文符号:?
         */
        String QUESTION_MARK = "?";

        /**
         * 符号：/
         */
        String INCLINED_ROD = "/";

        /**
         * 符号：.
         */
        String POINT = ".";

        /**
         * @
         */
        String AT = "@";

        /**
         * :
         */
        String COLON = ":";
    }






}
