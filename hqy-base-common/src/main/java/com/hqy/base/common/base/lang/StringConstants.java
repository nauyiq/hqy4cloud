package com.hqy.base.common.base.lang;

/**
 * 全局常量类
 * @author qiyuan.hong
 * @date 2022-02-15 23:23
 */
public interface StringConstants {

    String UID = "uid";

    String UNKNOWN = "unknown";

    String APPLICATION_JSON_UTF_8 = "application/json;charset=UTF-8";

    String INNER_IP = "127.0.0.1";

    String IPV6_LOCAL = "0:0:0:0:0:0:0:1";

    String WEBSOCKET = "websocket";

    String WEBSOCKET_PATH = Symbol.INCLINED_ROD + WEBSOCKET;

    String EMPTY = "";

    String FAVICON_ICO = "/favicon.ico";

    String DEFAULT = "default";

    String OS_NAME_KEY = "os.name";

    String OS_WINDOWS_PREFIX = "Windows";

    String OS_WIN_PREFIX = "win";

    String OS_ARCH_KEY = "os.arch";

    String OS_ARCH_PREFIX = "aarch64";

    String BOOTSTRAP = "bootstrap";



    interface Auth {

        /**
         * socket.io集群 握手的通道hash值
         */
        String SOCKET_MULTI_PARAM_KEY = "hash";

        /**
         * ssl 加密通道 keystore
         */
        String SOCKET_SSL_KEYSTORE_KEY = "socket.ssl.keystore";

        /**
         * ssl 加密通道 keystore password
         */
        String SOCKET_SSL_KEYSTORE_PASSWORD = "socket.ssl.keystore.password";

        /**
         * 认证请求头key
         */
        String AUTHORIZATION_KEY = "Authorization";

        /**
         * wtoken:websocket握手安全校验凭证
         */
        String SOCKET_AUTH_TOKEN = "wtoken";

        /**
         * socket.io 通道id,会话id
         */
        String BIZ_ID = "bizId";

        /**
         * JWT令牌前缀
         */
        String JWT_PREFIX = "bearer ";

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
        String JWT_AUTHORITIES_KEY = "token";

        /**
         * refresh_token
         */
        String REFRESH_TOKEN_KEY = "refresh_token";

        /**
         * 认证身份标识
         */
        String AUTHENTICATION_IDENTITY_KEY = "authenticationIdentity";

        /**
         * Oauth2模式
         */
        String GRANT_TYPE_KEY = "grant_type";

        /**
         * Oauth2 client_id
         */
        String CLIENT_ID = "client_id";
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

        /**
         * -
         */
        String RAIL = "-";

        /**
         * ,
         */
        String COMMA = ",";
    }

    interface File {

        String YML = ".yml";

        String YAML = ".yaml";

    }

    interface Host {
        String HTTP = "http://";
        String HTTPS = "https://";
        String FILE_HQY_HOST = "file.hongqy1024.cn";
        String LOCAL_HOST = HTTP + "localhost";
    }


    interface SocketProperties {

        String SOCKET_IO_PORT = "socket.port";

        /**
         * 是否启用集群模式
         */
        String ENABLE_MULTI_CLUSTER_NODES = "socket.multi.cluster.nodes";

        /**
         * 节点个数
         */
        String COUNT_MULTI_CLUSTER_NODES = "socket.count.cluster.nodes";

        /**
         * 当前项目hash节点值
         */
        String MULTI_CLUSTER_THIS_HASH = "socket.cluster.this.hash";

    }





}
