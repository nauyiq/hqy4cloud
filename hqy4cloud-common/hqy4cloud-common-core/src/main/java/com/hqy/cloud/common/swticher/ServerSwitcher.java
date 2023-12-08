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
            = new ServerSwitcher(153,"网关服务-是否启用HTTP限流结果持久化",true , MicroServiceConstants.GATEWAY);




}
