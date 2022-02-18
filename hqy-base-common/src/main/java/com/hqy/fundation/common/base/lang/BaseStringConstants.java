package com.hqy.fundation.common.base.lang;

/**
 * 全局常量类
 * @author qiyuan.hong
 * @date 2022-02-15 23:23
 */
public interface BaseStringConstants {


    /**
     * 默认的hash因子
     */
    String DEFAULT_HASH_FACTOR = "default";

    /**
     * nacos的注册元数据key
     */
    String NODE_INFO = "nodeInfo";

    /**
     * 英文符号:?
     */
    String QUESTION_MARK = "?";

    /**
     * 符号：/
     */
    String INCLINED_ROD = "/";

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
     * 字符串unknown
     */
    String UNKNOWN = "unknown";

    public static final String NACOS_NAMING_SERVICE = "nacos_naming_service";

}
