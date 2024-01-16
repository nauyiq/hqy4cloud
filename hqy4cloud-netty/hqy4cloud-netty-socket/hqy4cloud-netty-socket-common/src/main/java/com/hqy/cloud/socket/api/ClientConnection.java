package com.hqy.cloud.socket.api;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/12
 */
public interface ClientConnection {

    /**
     * 获取客户端连接的url
     * @return 连接的url
     */
    String getConnectUrl();

    /**
     * 获取建立连接的认证
     * @return 认证token
     */
    String getAuthorization();


    /**
     * 获取socket context path
     * @return context path
     */
    String getContextPath();


}
