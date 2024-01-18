package com.hqy.cloud.socket.cluster;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.registry.context.ProjectContext;
import com.hqy.cloud.socket.api.ClientConnection;
import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.cluster.support.ConnectBindModel;
import com.hqy.cloud.socket.model.SocketAuthorization;
import com.hqy.cloud.socket.model.SocketConnectionInfo;
import com.hqy.cloud.socket.model.SocketServerInfo;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.IpUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public abstract class AbstractSocketCluster implements SocketCluster {
    protected static final String AUTHORIZATION_REQUEST_KEY = "?Authorization=";


    @Override
    public ClientConnection getClientConnection(String bizId, SocketServer localServer, List<SocketServer> socketServers) {
        AssertUtil.notEmpty(bizId, "Socket Client request bizId should not be empty.");
        AssertUtil.notEmpty(socketServers, "Socket servers should not be empty.");

        // 由子类根据策略选择客户端连接到哪个服务.
        ConnectBindModel routerModel = chooseClientBindServer(bizId, localServer, socketServers);
        SocketServer socketServer = routerModel.getSocketServer();
        SocketServerInfo serverInfo = socketServer.getInfo();
        // 获取验证token.
        String authorization = localServer.getAuthorizationService().encryptAuthorization(SocketAuthorization.of(serverInfo.getApplicationName(), bizId));
        // 获取建立连接的url
        String connectUrl = getClientConnectionUrl(socketServer, authorization);

        // 由子类去构造客户端连接实现类
        return genConnection(routerModel, connectUrl, authorization, localServer.getMetadata().getContextPath());
    }

    @Override
    public SocketServer find(List<SocketServer> socketServers, SocketConnectionInfo connectionInfo) {
        if (CollectionUtils.isEmpty(socketServers)) {
            return null;
        }
        if (socketServers.size() == 1) {
            return socketServers.get(0);
        }
        return doFind(socketServers, connectionInfo);
    }

    @Override
    public List<SocketServer> find(List<SocketServer> socketServers, List<SocketConnectionInfo> connectionInfos) {
        if (CollectionUtils.isEmpty(socketServers)) {
            return Collections.emptyList();
        }
        if (socketServers.size() == 1) {
            return List.of(socketServers.get(0));
        }
        return doFind(socketServers, connectionInfos);
    }

    protected String getClientConnectionUrl(SocketServer socketServer, String authorization) {
        String connectUrl;
        if (CommonSwitcher.ENABLE_GATEWAY_SOCKET_AUTHORIZE.isOn()) {
            // 如果允许gateway路由socket服务.
            String host = StringConstants.Host.HTTP + IpUtil.getHostAddress();
            connectUrl = ProjectContext.getEnvironment().isDevEnvironment() ?
                    host + StrUtil.COLON + MicroServiceConstants.DEFAULT_PORT_GATEWAY : MicroServiceConstants.DEFAULT_ACCESS_DOMAIN_NAME;
        } else {
            connectUrl = socketServer.getAddress();
        }
        connectUrl = connectUrl + AUTHORIZATION_REQUEST_KEY + authorization;
        return connectUrl;
    }

    protected abstract ConnectBindModel chooseClientBindServer(String bizId, SocketServer localServer, List<SocketServer> socketServers);
    protected abstract ClientConnection genConnection(ConnectBindModel routerModel, String connectUrl, String authorization, String contextPath);
    protected abstract SocketServer doFind(List<SocketServer> socketServers, SocketConnectionInfo connectionInfo);
    protected abstract List<SocketServer> doFind(List<SocketServer> socketServers, List<SocketConnectionInfo> connectionInfos);
}
