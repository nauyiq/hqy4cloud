package com.hqy.cloud.socket.api;

import com.hqy.cloud.registry.common.context.CloseableService;
import com.hqy.cloud.socket.model.SocketServerInfo;
import com.hqy.cloud.socket.model.SocketServerMetadata;
import com.hqy.foundation.authorization.AuthorizationService;

/**
 * 标识一个socket服务
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
public interface SocketServer extends CloseableService {

    /**
     * 获取socket server 连接地址 ip:port
     * @return  ip:port
     */
    String getAddress();

    /**
     * 获取socket采用的认证service.
     * @return {@link AuthorizationService}
     */
    AuthorizationService getAuthorizationService();

    /**
     * 获取服务信息
     * @return socket服务信息 {@link SocketServerInfo}
     */
    SocketServerInfo getInfo();

    /**
     * 获取socket服务元数据
     * @return socket服务元数据
     */
    SocketServerMetadata getMetadata();

    /**
     * 启动socket服务
     */
    void start();






}
