package com.hqy.cloud.socket.cluster.client;

import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.model.SocketConnectionInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public interface Client {

    /**
     * 获取一个socket服务
     * @param application socket服务名.
     * @param info        连接参数.
     * @return            socket服务
     */
    SocketServer getSocketServer(String application, SocketConnectionInfo info);

    /**
     * 获取客户端id连接的socket服务
     * @param application socket服务名.
     * @param bizId       业务id
     * @return            socket服务
     */
    SocketServer findSocketServer(String application, String bizId);

    /**
     * 批量获取客户端id连接的socket服务
     * @param application socket服务名
     * @param bizIds      业务id
     * @return            socket服务
     */
    Map<String, SocketServer> findSocketServers(String application, Set<String> bizIds);

    /**
     * 批量获取socket服务
     * @param application socket服务名.
     * @param infos       连接参数.
     * @return            socket服务列表.
     */
    List<SocketServer> getSocketServers(String application, List<SocketConnectionInfo> infos);

    /**
     * 获取某个socket服务的全部实例
     * @param application socket服务名
     * @return           某个socket服务的全部实例
     */
    List<SocketServer> getAllSocketServer(String application);

}
