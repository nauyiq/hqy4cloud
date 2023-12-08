package com.hqy.cloud.common.base.lang;

/**
 * 全局常量类
 * @author qiyuan.hong
 * @date 2022-02-15 23:23
 */
public interface StringConstants {

    String UID = "uid";
    String UPGRADE = "Upgrade";
    String UNKNOWN = "unknown";
    String WEBSOCKET = "websocket";
    String WEBSOCKET_PATH = Symbol.INCLINED_ROD + WEBSOCKET;
    String APPLICATION_JSON_UTF_8 = "application/json;charset=UTF-8";
    String INNER_IP = "127.0.0.1";
    String IPV6_LOCAL = "0:0:0:0:0:0:0:1";
    String EMPTY = "";
    String FAVICON_ICO = "/favicon.ico";
    String DEFAULT = "default";
    String OS_NAME_KEY = "os.name";
    String OS_WINDOWS_PREFIX = "Windows";
    String OS_WIN_PREFIX = "win";
    String OS_ARCH_KEY = "os.arch";
    String OS_ARCH_PREFIX = "aarch64";
    String BOOTSTRAP = "bootstrap";
    String APPLICATION = "application";
    String HTTP = "http";
    String SHOULD_NOT_BE_NULL = "should not be null.";
    String SHOULD_NOT_BE_EMPTY = "should not be empty.";
    String DEFAULT_VERSION = "1.0.0";
    String NULL = "null";
    String TRUE = "1";
    String FALSE = "0";
    String DO_PNG = ".png";
    String CONSUMER = "消费者";
    String PROVIDER = "生产者";

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

        String AT_AT = "@@";

        /**
         * :
         */
        String COLON = ":";

        /**
         * ::
         */
        String UNION = "::";

        /**
         * -
         */
        String RAIL = "-";

        /**
         * ,
         */
        String COMMA = ",";

        /**
         * %
         */
        String PERCENT = "%";

        /**
         * =
         */
        String EQUALS = "=";

        /**
         * &
         */
        String AND = "&";

        String LEFT_BRACKET = "(";

        String RIGHT_BRACKET = ")";

    }

    interface Host {
        String HTTP = "http://";
        String HTTPS = "https://";
        String FILE_HQY_HOST = "file.hongqy1024.cn";
        String LOCAL_HOST = HTTP + "localhost";
        String HTTPS_FILE_ACCESS = HTTPS + FILE_HQY_HOST;
        String API_GATEWAY = "api.hongqy1024.cn";
        String HTTPS_API_GATEWAY = HTTPS + API_GATEWAY;
    }





}
