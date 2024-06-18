package com.hqy.cloud.netty.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import com.hqy.cloud.auth.utils.AuthUtils;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.socket.api.ClientConnection;
import com.hqy.cloud.socket.cluster.SocketCluster;
import com.hqy.cloud.socket.cluster.client.support.SocketClient;
import com.hqy.cloud.socket.cluster.support.SocketClusters;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/15
 */
@Slf4j
@RequiredArgsConstructor
public abstract class SocketIoEndpoint {
    private final SocketIoSocketServer ioSocketServer;
    private final SocketClient client;

    @ResponseBody
    @GetMapping("/connection")
    public R<ClientConnection> getSocketIoConnection(HttpServletRequest request) {
        Long currentUserId = AuthUtils.getCurrentUserId();
        String applicationName = ioSocketServer.getInfo().getApplicationName();
        String bizId = currentUserId.toString();
        SocketIOServer socketIOServer = ioSocketServer.getSocketIOServer();
        String clusterType = ioSocketServer.getMetadata().getClusterType();
        SocketCluster cluster = SocketClusters.cluster(clusterType);
        ClientConnection clientConnection = cluster.getClientConnection(bizId, ioSocketServer, client.getAllSocketServer(applicationName));
        if (clientConnection == null) {
            log.warn("Failed execute to get socket connection, applicationL {}, bizId {}, clusterType: {}.", applicationName, bizId, clusterType);
            return R.failed();
        }
        return R.ok(clientConnection);
    }




}
