package com.hqy.cloud.socket.cluster;

import com.hqy.cloud.socket.api.ClientConnection;
import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.model.SocketConnectionInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SocketCluster
 * 提供socket集群能力, 比如生成客户端连接、 寻址客户端在集群中的位置.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public interface SocketCluster {

    /**
     * 初始化cluster
     * @param localServer   本地socket服务， 即自身
     * @param socketServers 当前socket服务列表
     */
    default void init(SocketServer localServer, List<SocketServer> socketServers) {

    }

    /**
     * 获取路由器名称
     * @return 路由器名
     */
    String getClusterName();

    /**
     * 生成建立建立的url. 有对应的路由类型生成相关的请求连接,
     * @param bizId         客户端业务唯一id
     * @param localServer   本地socket服务， 即自身
     * @param socketServers 当前socket服务列表
     * @return              {@link ClientConnection}
     */
    ClientConnection getClientConnection(String bizId, SocketServer localServer, List<SocketServer> socketServers);


    /**
     * 通过业务id获取socket服务
     * @param applicationName 服务名
     * @param bizId           业务id
     * @param socketServers   socket服务列表
     * @return                socket服务
     */
    SocketServer getSocketServer(String applicationName, String bizId, List<SocketServer> socketServers);

    /**
     * 批量获取socket服务
     * @param applicationName 服务名
     * @param bizIdSet        bizId集合
     * @param socketServers   socket服务列表
     * @return                socket服务
     */
    Map<String, SocketServer> getSocketServers(String applicationName, Set<String> bizIdSet, List<SocketServer> socketServers);


    /**
     * 选择一个合适的socket server
     * @param socketServers  socket服务列表
     * @param connectionInfo 连接信息
     * @return               socket服务
     */
    SocketServer find(List<SocketServer> socketServers, SocketConnectionInfo connectionInfo);

    /**
     * 根据不同的连接信息列表一次批量选择适合的服务端
     * @param socketServers   socket服务列表
     * @param connectionInfos 连接信息列表。
     * @return                socket服务列表
     */
    List<SocketServer> find(List<SocketServer> socketServers, List<SocketConnectionInfo> connectionInfos);




}
