package com.hqy.cloud.socket.cluster.client;

import com.hqy.cloud.socket.cluster.router.ClusterRouters;
import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.model.SocketConnectionInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
@Slf4j
public abstract class AbstractSocketDirectory implements SocketDirectory {
    protected final String socketServerApplication;
    protected volatile List<SocketServer> socketServers = Collections.emptyList();

    public AbstractSocketDirectory(String socketServerApplication) {
        this.socketServerApplication = socketServerApplication;
    }

    public void setSocketServers(List<SocketServer> socketServers) {
        this.socketServers = socketServers;
    }

    @Override
    public String applicationName() {
        return socketServerApplication;
    }

    @Override
    public List<SocketServer> list() {
        return this.socketServers;
    }

    @Override
    public SocketServer getServer(SocketConnectionInfo connectionInfo) {
        return ClusterRouters.route(list(), connectionInfo);
    }

    @Override
    public List<SocketServer> getServers(List<SocketConnectionInfo> connectionInfos) {
        return ClusterRouters.route(list(), connectionInfos);
    }
}
