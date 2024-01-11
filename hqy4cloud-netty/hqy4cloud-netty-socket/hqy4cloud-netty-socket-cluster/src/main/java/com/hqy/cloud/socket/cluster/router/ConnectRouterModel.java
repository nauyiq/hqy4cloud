package com.hqy.cloud.socket.cluster.router;

import com.hqy.cloud.socket.api.SocketServer;

import java.util.Collections;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public record ConnectRouterModel(SocketServer socketServer,
                                 Map<String, String> params) {


    public static ConnectRouterModel of(SocketServer socketServer) {
        return new ConnectRouterModel(socketServer, Collections.emptyMap());
    }

    public static ConnectRouterModel of(SocketServer socketServer, Map<String, String> params) {
        return new ConnectRouterModel(socketServer, params);
    }


}
