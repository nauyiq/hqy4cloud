package com.hqy.cloud.socket.cluster.router;

import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.model.SocketConnectionInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public abstract class AbstractSocketRouter implements SocketRouter {

    @Override
    public SocketServer choose(List<SocketServer> socketServers, SocketConnectionInfo connectionInfo) {
        List<SocketServer> choose = choose(socketServers, List.of(connectionInfo));
        return CollectionUtils.isEmpty(choose) ? null : choose.get(0);
    }

    @Override
    public List<SocketServer> choose(List<SocketServer> socketServers, List<SocketConnectionInfo> connectionInfos) {
        if (CollectionUtils.isEmpty(socketServers)) {
            return Collections.emptyList();
        }
        if (socketServers.size() == 1) {
            return List.of(socketServers.get(0));
        }
        return doChoose(socketServers, connectionInfos);
    }

    /**
     * 交给子类根据不同策略进行socket路由
     * @param socketServers   socket服务列表
     * @param connectionInfos 连接信息列表
     * @return                socket服务列表
     */
    protected abstract List<SocketServer> doChoose(List<SocketServer> socketServers, List<SocketConnectionInfo> connectionInfos);
}
