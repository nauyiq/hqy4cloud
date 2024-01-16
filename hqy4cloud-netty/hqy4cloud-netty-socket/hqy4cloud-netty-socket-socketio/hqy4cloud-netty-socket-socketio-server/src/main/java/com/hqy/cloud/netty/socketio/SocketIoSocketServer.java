package com.hqy.cloud.netty.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import com.hqy.cloud.netty.socketio.deloyer.SocketIoServerModel;
import com.hqy.cloud.netty.socketio.listener.SocketIoEventListener;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.socket.SocketConstants;
import com.hqy.cloud.socket.cluster.ClusterSocketServer;
import com.hqy.foundation.authorization.AuthorizationService;

import java.util.Set;

/**
 * SocketIoSocketServer.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public class SocketIoSocketServer extends ClusterSocketServer {
    private final SocketIOServer socketIOServer;
    private final SocketIoServerModel socketIoServerModel;
    private volatile boolean destroy = false;

    public SocketIoSocketServer(ApplicationModel model, SocketIOServer socketIOServer, SocketIoServerModel socketIoServerModel) {
        super(model, socketIoServerModel.getPort());
        this.socketIOServer = socketIOServer;
        this.socketIoServerModel = socketIoServerModel;
    }

    @Override
    public void initialize() {
        Set<SocketIoEventListener> eventListeners = socketIoServerModel.getEventListeners();
        eventListeners.forEach(this::addEventListener);
        // 初始化元数据参数.
        getModel().setParameter(SocketConstants.ENABLED_SOCKET_SERVER_CLUSTER, socketIoServerModel.getCluster().toString());
        getModel().setParameter(SocketConstants.SOCKET_SERVER_CONTEXT, socketIoServerModel.getContext());
        getModel().setParameter(SocketConstants.SOCKET_CLUSTER_TYPE, getRouterName());
        super.initialize();
    }

    @Override
    public boolean isAvailable() {
        return !destroy;
    }

    @Override
    public void destroy() {
        destroy = true;
        socketIOServer.stop();
    }

    @Override
    protected void onStart() {
        this.socketIOServer.start();
    }

    public SocketIOServer getSocketIOServer() {
        return socketIOServer;
    }

    public SocketIoServerModel getSocketIoModel() {
        return socketIoServerModel;
    }

    @SuppressWarnings("unchecked")
    public void addEventListener(SocketIoEventListener eventListener) {
        this.socketIOServer.addEventListener(eventListener.eventName(), eventListener.eventClass(), eventListener.getDataListener());
    }

    @Override
    public AuthorizationService getAuthorizationService() {
        return socketIoServerModel.getAuthorizationService();
    }
}
