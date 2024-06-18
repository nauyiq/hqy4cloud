package com.hqy.cloud.common.base.project;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 微服务的模块定义。用在@ThriftService注解上面<br>
 * 所有的微服务模块 必须在此常量定义.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 10:26
 */
public class MicroServiceConstants {


    /**
     * 全局网关gateway服务
     */
    public static final String GATEWAY = "hqy4cloud-gateway";
    public static final int DEFAULT_PORT_GATEWAY = 9527;
    public static final String DEFAULT_ACCESS_DOMAIN_NAME = "https://api.hongqy1024.cn";
    public static final ProjectInfo GATEWAY_INFO = new ProjectInfo("网关服务", GATEWAY);

    /**
     * 基础采集服务
     */
    public static final String COMMON_COLLECTOR = "hqy4cloud-base-collection-service";
    public static final ProjectInfo COLLECTOR_INFO = new ProjectInfo("采集服务", COMMON_COLLECTOR);

    /**
     * 基础通讯服务
     */
    public static final String COMMUNICATION_SERVICE = "hqy4cloud-base-communication-service";
    public static final ProjectInfo COMMUNICATION_INFO = new ProjectInfo("通讯服务", COMMUNICATION_SERVICE);


    /**
     * 基础id生成服务
     */
    public static final String ID_SERVICE = "hqy4cloud-base-id-service";
    public static final ProjectInfo ID_INFO = new ProjectInfo("分布式id服务", ID_SERVICE);

    /**
     * 账号-授权服务
     */
    public static final String ACCOUNT_SERVICE = "hqy4cloud-auth-account-service";
    public static final ProjectInfo ACCOUNT_AUTH_INFO = new ProjectInfo("账号授权服务", ACCOUNT_SERVICE);

    /**
     * 后台管理服务
     */
    public static final String ADMIN_SERVICE = "hqy4cloud-auth-admin-service";
    public static final ProjectInfo ADMIN_INFO = new ProjectInfo("后台管理服务", ADMIN_SERVICE);


    /**
     * 聊天消息服务
     */
    public static final String MESSAGE_NETTY_SERVICE = "hqy4cloud-apps-message-service";
    public static final ProjectInfo MESSAGE_NETTY_INFO = new ProjectInfo("聊天消息服务", MESSAGE_NETTY_SERVICE);

    /**
     * 博客服务.
     */
    public static final String BLOG_SERVICE = "hqy4cloud-apps-blog-service";
    public static final ProjectInfo BLOG_INFO = new ProjectInfo("博客服务", BLOG_SERVICE);

    /**
     * 服务列表
     */
    public static final List<ProjectInfo> SERVICES = Arrays.asList(GATEWAY_INFO, COLLECTOR_INFO,
            ACCOUNT_AUTH_INFO, MESSAGE_NETTY_INFO, COMMUNICATION_INFO, BLOG_INFO, ADMIN_INFO, ID_INFO);

    public static final Map<String, String> ALIAS_MAP = new ConcurrentHashMap<>();

    static {
        ALIAS_MAP.put(GATEWAY, GATEWAY_INFO.getName());
        ALIAS_MAP.put(COMMON_COLLECTOR, COLLECTOR_INFO.getName());
        ALIAS_MAP.put(COMMUNICATION_SERVICE, COMMUNICATION_INFO.getName());
        ALIAS_MAP.put(ID_SERVICE, ID_INFO.getName());
        ALIAS_MAP.put(ACCOUNT_SERVICE, ACCOUNT_AUTH_INFO.getName());
        ALIAS_MAP.put(ADMIN_SERVICE, ADMIN_INFO.getName());
        ALIAS_MAP.put(MESSAGE_NETTY_SERVICE, MESSAGE_NETTY_INFO.getName());
        ALIAS_MAP.put(BLOG_SERVICE, BLOG_INFO.getName());
    }

    public static class SocketContextPath {
        public static final String MESSAGE_SERVICE = "/message/websocket";
    }


}
