package com.hqy.cloud.netty.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import com.hqy.cloud.netty.socketio.deloyer.SocketIoModel;
import com.hqy.cloud.netty.socketio.listener.SocketIoEventListener;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.socket.cluster.server.ClusterSocketServer;
import com.hqy.cloud.socket.SocketConstants;

import java.util.Set;

/**
 * SocketIoSocketServer.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public class SocketIoSocketServer extends ClusterSocketServer {
    private final SocketIOServer socketIOServer;
    private final SocketIoModel socketIoModel;
    private volatile boolean destroy = false;

    protected SocketIoSocketServer(ApplicationModel model, SocketIOServer socketIOServer, SocketIoModel socketIoModel) {
        super(model, socketIoModel.getPort());
        this.socketIOServer = socketIOServer;
        this.socketIoModel = socketIoModel;
    }

    @Override
    public void initialize() {
        Set<SocketIoEventListener> eventListeners = socketIoModel.getEventListeners();
        eventListeners.forEach(this::addEventListener);
        getModel().setParameter(SocketConstants.ENABLED_SOCKET_SERVER_CLUSTER, socketIoModel.getCluster().toString());
        getModel().getParameter(SocketConstants.SOCKET_SERVER_CONTEXT, socketIoModel.getContext());
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
        super.onStart();
        this.socketIOServer.start();
    }

    public SocketIOServer getSocketIOServer() {
        return socketIOServer;
    }

    public SocketIoModel getSocketIoModel() {
        return socketIoModel;
    }

    @SuppressWarnings("unchecked")
    public void addEventListener(SocketIoEventListener eventListener) {
        this.socketIOServer.addEventListener(eventListener.eventName(), eventListener.eventClass(), eventListener.getDataListener());
    }
}
