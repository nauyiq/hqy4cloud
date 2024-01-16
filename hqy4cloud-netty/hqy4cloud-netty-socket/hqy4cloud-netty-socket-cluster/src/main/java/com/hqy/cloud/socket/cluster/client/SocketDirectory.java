package com.hqy.cloud.socket.cluster.client;

import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.model.SocketConnectionInfo;

import java.util.List;

/**
 * socket目录, 用于搜索路由socket服务
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public interface SocketDirectory  {

    /**
     * 获取服务名
     * @return 当前socket服务名
     */
    String applicationName();

    /**
     * 获取集群类型
     * @return cluster type
     */
    String getClusterType();

    /**
     * 获取当前socket服务全部健康实例.
     * @return socket服务列表
     */
    List<SocketServer> list();


    /**
     * 根据连接信息从服务列表中获取一个socket服务.
     * @param connectionInfo 连接信息.
     * @return               socket服务信息
     */
    SocketServer getServer(SocketConnectionInfo connectionInfo);

    /**
     * 批量获取socket服务
     * @param connectionInfos 连接信息
     * @return                socket服务信息
     */
    List<SocketServer> getServers(List<SocketConnectionInfo> connectionInfos);


}
