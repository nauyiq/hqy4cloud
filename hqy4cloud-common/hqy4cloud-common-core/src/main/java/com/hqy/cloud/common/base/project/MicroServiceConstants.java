package com.hqy.cloud.common.base.project;

import java.util.Arrays;
import java.util.List;

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
    public static final String GATEWAY = "hqy4cloud-gateway-service";
    public static final ProjectInfo GATEWAY_INFO = new ProjectInfo("网关服务", GATEWAY);

    /**
     * 通用的采集服务
     */
    public static final String COMMON_COLLECTOR = "hqy4cloud-base-collection-service";
    public static final ProjectInfo COLLECTOR_INFO = new ProjectInfo("采集服务", COMMON_COLLECTOR);

    /**
     * 通用通讯服务
     */
    public static final String COMMUNICATION_SERVICE = "hqy4cloud-base-communication-service";
    public static final ProjectInfo COMMUNICATION_INFO = new ProjectInfo("通讯服务", COMMUNICATION_SERVICE);

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
    public static final List<ProjectInfo> SERVICES = Arrays.asList(GATEWAY_INFO, COLLECTOR_INFO, ACCOUNT_AUTH_INFO, MESSAGE_NETTY_INFO, COMMUNICATION_INFO, BLOG_INFO, ADMIN_INFO);

    public static final String DEMO_ORDER_SERVICE = "demo-order-service";

    public static final String DEMO_STORAGE_SERVICE = "demo-storage-service";

    public static final String DEMO_WALLET_SERVICE = "demo-wallet-service";


    /**
     * socket.io contextPath
     */
    public static class SocketContextPath {

        /**
         * 消息服务的contextPath
         */
        public static final String MESSAGE_SERVICE = "/message/websocket";

    }


}
