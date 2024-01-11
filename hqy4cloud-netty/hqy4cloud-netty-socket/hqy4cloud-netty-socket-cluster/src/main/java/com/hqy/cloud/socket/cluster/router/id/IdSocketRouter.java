package com.hqy.cloud.socket.cluster.router.id;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.socket.cluster.router.AbstractSocketRouter;
import com.hqy.cloud.socket.cluster.router.ConnectRouterModel;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.socket.SocketConstants;
import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.model.SocketConnectionInfo;
import com.hqy.cloud.socket.model.SocketServerInfo;
import com.hqy.foundation.socket.IdRouterService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 基于hash策略的路由器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
@Slf4j
public class IdSocketRouter extends AbstractSocketRouter {
    protected static final String NAME = "idRouter";
    private final IdRouterService idRouterService;

    public IdSocketRouter(IdRouterService idRouterService) {
        this.idRouterService = idRouterService;
    }

    @Override
    public String getRouterName() {
        return NAME;
    }

    @Override
    protected List<SocketServer> doChoose(List<SocketServer> socketServers, List<SocketConnectionInfo> connectionInfos) {
        AssertUtil.notEmpty(connectionInfos, "Connection info should not be null.");
        // 获取连接的应用名
        String applicationName = socketServers.get(0).getInfo().getApplicationName();
        // 获取连接的id列表.
        List<String> ids = connectionInfos.stream().map(connectionInfo -> connectionInfo.getParameter(SocketConstants.SOCKET_CLUSTER_ROUTER_ID_KEY, StrUtil.EMPTY)).toList();
        Map<Integer, String> hashAddress = idRouterService.getAddress(applicationName, new HashSet<>(ids));
        List<SocketServer> results = new ArrayList<>(connectionInfos.size());
        for (SocketConnectionInfo connectionInfo : connectionInfos) {
            int hash = connectionInfo.getParameter(SocketConstants.SOCKET_CLUSTER_ROUTER_ID_KEY, 0);
            String address = hashAddress.get(0);
            results.add(find(socketServers, address));
        }
        return results;
    }

    @Override
    public ConnectRouterModel getConnectServerModel(String bizId, List<SocketServer> socketServers) {
        AssertUtil.notEmpty(bizId, "Socket Client request bizId should not be empty.");
        AssertUtil.notEmpty(socketServers, "Socket servers should not be empty.");
        int code = Math.abs(bizId.hashCode());
        SocketServer socketServer = socketServers.get(ThreadLocalRandom.current().nextInt(socketServers.size()));
        SocketServerInfo info = socketServer.getInfo();
        Map<String, String> params = new HashMap<>(2);
        params.put(SocketConstants.SOCKET_CLUSTER_ROUTER_ID_KEY, info.getId());
        params.put(SocketConstants.SOCKET_SERVER_CONTEXT, info.getMetadata().getContextPath());
        return ConnectRouterModel.of(socketServer, params);
    }

    private SocketServer find(List<SocketServer> socketServers, String address) {
        SocketServer find = null;
        for (SocketServer socketServer : socketServers) {
            if (address.equals(socketServer.getAddress())) {
                find = socketServer;
                break;
            }
        }

        if (find == null && CommonSwitcher.ENABLE_WHEN_SOCKET_ROUTER_NOT_USING_RANDOM.isOn()) {
            find = socketServers.get(ThreadLocalRandom.current().nextInt(socketServers.size()));
        }

        return find;
    }

}
