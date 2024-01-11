package com.hqy.cloud.socket.cluster.router;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.model.SocketConnectionInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
@Slf4j
public class ClusterRouters {

    private static final Map<String, SocketRouter> ROUTERS = new ConcurrentHashMap<>(2);
    private static final SocketRouter DEFAULT_ROUTERS = new AbstractSocketRouter() {
        @Override
        protected List<SocketServer> doChoose(List<SocketServer> socketServers, List<SocketConnectionInfo> connectionInfos) {
            return socketServers;
        }

        @Override
        public String getRouterName() {
            return StringConstants.DEFAULT;
        }

        @Override
        public ConnectRouterModel getConnectServerModel(String bizId, List<SocketServer> socketServers) {
            return ConnectRouterModel.of(socketServers.get(0));
        }
    };

    /**
     * 注册某个socket服务使用的socket路由
     * @param application 服务名.
     * @param router      路由.
     */
    public static void registerRouter(String application, SocketRouter router) {
        AssertUtil.notEmpty(application, "Application name should not be empty.");
        AssertUtil.notNull(router, "Socket router should not be null.");
        log.info("Socket server {} register router, router name {}.", application, router.getRouterName());
        ROUTERS.put(application, router);
    }

    /**
     * 获取一个socket服务路由
     * @param application socket服务名
     * @return            socket服务路由
     */
    public static SocketRouter router(String application) {
        return ROUTERS.get(application);
    }


    public static SocketServer route(List<SocketServer> socketServers, SocketConnectionInfo connectionInfo) {
        if (CollectionUtils.isEmpty(socketServers)) {
            return null;
        }
        if (socketServers.size() == 1) {
            return socketServers.get(0);
        }
        String applicationName = socketServers.get(0).getInfo().getApplicationName();
        // 获取路由器.
        SocketRouter router = ROUTERS.getOrDefault(applicationName, DEFAULT_ROUTERS);
        return router.choose(socketServers, connectionInfo);
    }


    public static List<SocketServer> route(List<SocketServer> socketServers, List<SocketConnectionInfo> connectionInfos) {
        if (CollectionUtils.isEmpty(socketServers)) {
            return Collections.emptyList();
        }
        if (socketServers.size() == 1) {
            return socketServers;
        }
        String applicationName = socketServers.get(0).getInfo().getApplicationName();
        // 获取路由器.
        SocketRouter router = ROUTERS.getOrDefault(applicationName, DEFAULT_ROUTERS);
        return router.choose(socketServers, connectionInfos);
    }











}
