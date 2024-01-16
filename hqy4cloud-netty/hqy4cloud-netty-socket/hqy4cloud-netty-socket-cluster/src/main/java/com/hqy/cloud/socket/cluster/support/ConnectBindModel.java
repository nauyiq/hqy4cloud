package com.hqy.cloud.socket.cluster.support;

import com.hqy.cloud.common.base.Parameters;
import com.hqy.cloud.socket.api.SocketServer;

import java.util.Collections;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public class ConnectBindModel extends Parameters {

    private final SocketServer socketServer;

    public ConnectBindModel(SocketServer socketServer) {
        this.socketServer = socketServer;
    }

    public ConnectBindModel(SocketServer socketServer, Map<String, String> params) {
        this.socketServer = socketServer;
        this.parameters = params;
    }

    public static ConnectBindModel of(SocketServer socketServer) {
        return new ConnectBindModel(socketServer, Collections.emptyMap());
    }

    public static ConnectBindModel of(SocketServer socketServer, Map<String, String> params) {
        return new ConnectBindModel(socketServer, params);
    }

    public SocketServer getSocketServer() {
        return socketServer;
    }
}
