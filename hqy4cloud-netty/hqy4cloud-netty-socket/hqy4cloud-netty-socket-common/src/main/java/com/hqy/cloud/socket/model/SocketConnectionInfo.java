package com.hqy.cloud.socket.model;

import com.hqy.cloud.common.base.Parameters;
import com.hqy.cloud.socket.api.ClientConnection;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public class SocketConnectionInfo extends Parameters {
    private final ClientConnection connection;

    public SocketConnectionInfo(ClientConnection connection) {
        this.connection = connection;
    }

    public static SocketConnectionInfo of(ClientConnection connectUrl) {
        return new SocketConnectionInfo(connectUrl);
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public String getConnectionUrl() {
        return connection.getConnectUrl();
    }

    public String getAuthorization() {
        return connection.getAuthorization();
    }

    public String getContext() {
        return connection.getContextPath();
    }



}
