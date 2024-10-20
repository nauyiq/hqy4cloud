package com.hqy.cloud.common.swticher;

import com.hqy.cloud.common.base.project.MicroServiceConstants;

/**
 * 服务级别的开关，开关只针对注册的服务启作用
 * @author qy
 * @date 2021-07-30 11:28
 */
public class ServerSwitcher extends CommonSwitcher {
    private final String serverName;

    protected ServerSwitcher(int id, String name, boolean status, String serverName) {
        super(id, name, status);
        this.serverName = serverName;
    }

    protected ServerSwitcher(int id, String name, boolean status, boolean registerActuator, String serverName) {
        super(id, name, status, registerActuator);
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }

    /**
     * 节点(Web)-是否启用http请求体 可重复读的请求包装过滤器 20210630 默认开
     */
    public static final ServerSwitcher ENABLE_REPEAT_READABLE_HTTP_REQUEST_WRAPPER_FILTER =
            new ServerSwitcher(101,"网关服务-是否启用http请求体 可重复读的请求包装过滤器",true, MicroServiceConstants.GATEWAY);


    /**
     * 节点-是否启用HTTP限流器，限制请求频度（1分钟一个ip 限制若干次请求）
     */
    public static final ServerSwitcher ENABLE_HTTP_THROTTLE_VALVE =
            new ServerSwitcher(150,"网关服务-是否启用通用HTTP限流器",true, MicroServiceConstants.GATEWAY);

    /**
     * 节点-是否启用(URI 和请求参数中xss攻击防范)参数校验的限流器...
     */
    public static final ServerSwitcher ENABLE_HTTP_THROTTLE_SECURITY_CHECKING =
            new ServerSwitcher(151,"网关服务-是否启用HTTP限流安全侦测(黑客)",true, MicroServiceConstants.GATEWAY);


    /**
     * 节点-是否所有项目共享超限统计计数方式
     */
    public static final ServerSwitcher ENABLE_SHARE_IP_OVER_REQUEST_STATISTICS =
            new ServerSwitcher(152, "网关服务-是否所有项目共享超限统计计数方式", true, MicroServiceConstants.GATEWAY);


    /**
     * 节点-是否启用HTTP限流结果持久化
     */
    public static final ServerSwitcher ENABLE_HTTP_THROTTLE_PERSISTENCE
            = new ServerSwitcher(153,"网关服务-是否启用HTTP限流结果持久化",true, MicroServiceConstants.GATEWAY);

    /**
     * 节点-是否采用自定义的网关负载均衡策略
     */
    public static final ServerSwitcher ENABLE_CUSTOMER_GATEWAY_LOAD_BALANCE = new ServerSwitcher(154, "节点-是否采用自定义的网关负载均衡策略", true, MicroServiceConstants.GATEWAY);

    /**
     * 节点-网关负载均衡websocket项目时是否重新路由端口
     */
    public static final ServerSwitcher ENABLE_GATEWAY_WEBSOCKET_ROUTER_PORTER = new ServerSwitcher(155, "节点-网关负载均衡websocket项目时是否重新路由端口", true, MicroServiceConstants.GATEWAY);

    /**
     * 节点-是否基于http-restful判断请求是读请求还是写请求
     */
    public static final ServerSwitcher ENABLE_BASE_HTTP_RESTFUL_CHECK_REQUEST_PERMISSION = new ServerSwitcher(156, "是否开启基于http-restful判断请求的读写权限", true, MicroServiceConstants.GATEWAY);


    /**
     * 采集服务-是否采集来自采集服务本身的异常
     * 采集服务本身发生异常时，是否采集‘采集服务‘的异常
     */
    public static final ServerSwitcher ENABLE_COLLECT_COLLECTION_SERVICE_EXCEPTION
            = new ServerSwitcher(160, "采集服务-是否采集'采集服务'本身的异常", false, MicroServiceConstants.COMMON_COLLECTOR);

    /**
     * 节点-是否开启用户token生成次数限制，即当用于重复生成token时，将旧token移除
     */
    public static final ServerSwitcher ENABLE_LIMIT_ACCESS_TOKEN_GENERATE_COUNT = new ServerSwitcher(160, "是否开启用户token生成次数限制", true, MicroServiceConstants.ACCOUNT_SERVICE);




}
