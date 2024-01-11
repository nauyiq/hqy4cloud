package com.hqy.cloud.socket.cluster.client;

import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.model.SocketConnectionInfo;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public interface Client {

    /**
     * 生成连接socket服务的连接信息
     * @param application socket服务名
     * @param bizId       客户端业务唯一id
     * @return            socket连接信息.
     */
    SocketConnectionInfo getConnection(String application, String bizId);

    /**
     * 获取一个socket服务
     * @param application socket服务名.
     * @param info        连接参数.
     * @return            socket服务
     */
    SocketServer getSocketServer(String application, SocketConnectionInfo info);

    /**
     * 批量获取socket服务
     * @param application socket服务名.
     * @param infos       连接参数.
     * @return            socket服务列表.
     */
    List<SocketServer> getSocketServers(String application, List<SocketConnectionInfo> infos);



}
