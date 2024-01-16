package com.hqy.cloud.socket.cluster.client;

import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.cluster.SocketCluster;
import com.hqy.cloud.socket.cluster.support.SocketClusters;
import com.hqy.cloud.socket.model.SocketConnectionInfo;
import com.hqy.cloud.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
    private String clusterType;

    public AbstractSocketDirectory(String socketServerApplication) {
        this.socketServerApplication = socketServerApplication;
    }

    public void setSocketServers(List<SocketServer> socketServers) {
        this.socketServers = socketServers;
        if (CollectionUtils.isNotEmpty(socketServers) && StringUtils.isNotBlank(clusterType)) {
            // 初始化集群类型
            SocketServer socketServer = socketServers.get(0);
            // TODO 暂时先取默认第一个服务的集群类型作为当前的服务的集群类型, 因为理论上集群中所有的节点的集群类型都是同一个
            this.clusterType = socketServer.getMetadata().getClusterType();
        }

    }

    @Override
    public String applicationName() {
        return socketServerApplication;
    }

    @Override
    public String getClusterType() {
        return this.clusterType;
    }

    @Override
    public List<SocketServer> list() {
        return this.socketServers;
    }

    @Override
    public SocketServer getServer(SocketConnectionInfo connectionInfo) {
        SocketCluster cluster = SocketClusters.cluster(clusterType);
        AssertUtil.notNull(cluster, "Not found cluster by " + clusterType);
        return cluster.find(list(), connectionInfo);
    }

    @Override
    public List<SocketServer> getServers(List<SocketConnectionInfo> connectionInfos) {
        SocketCluster cluster = SocketClusters.cluster(clusterType);
        AssertUtil.notNull(cluster, "Not found cluster by " + clusterType);
        return cluster.find(list(), connectionInfos);
    }
}
