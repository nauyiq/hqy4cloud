package com.hqy.cloud.socket.cluster.router;

import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.model.SocketConnectionInfo;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public interface SocketRouter {

    /**
     * 获取连接服务端的请求的参数
     * @param bizId         客户端业务唯一id
     * @param socketServers 当前socket服务列表
     * @return              {@link ConnectRouterModel}
     */
    ConnectRouterModel getConnectServerModel(String bizId, List<SocketServer> socketServers);

    /**
     * 获取路由器名称
     * @return 路由器名
     */
    String getRouterName();

    /**
     * 选择一个合适的socket server
     * @param socketServers  socket服务列表
     * @param connectionInfo 连接信息
     * @return               socket服务
     */
    SocketServer choose(List<SocketServer> socketServers, SocketConnectionInfo connectionInfo);

    /**
     * 根据不同的连接信息列表一次批量选择适合的服务端
     * @param socketServers   socket服务列表
     * @param connectionInfos 连接信息列表。
     * @return                socket服务列表
     */
    List<SocketServer> choose(List<SocketServer> socketServers, List<SocketConnectionInfo> connectionInfos);






}
