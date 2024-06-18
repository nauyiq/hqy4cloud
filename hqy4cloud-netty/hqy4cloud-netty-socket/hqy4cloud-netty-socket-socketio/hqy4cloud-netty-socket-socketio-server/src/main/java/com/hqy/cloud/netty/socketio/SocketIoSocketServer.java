package com.hqy.cloud.netty.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import com.hqy.cloud.netty.socketio.deloyer.SocketIoServerModel;
import com.hqy.cloud.netty.socketio.listener.SocketIoEventListener;
import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.api.ServiceInstance;
import com.hqy.cloud.registry.common.context.BeanRepository;
import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import com.hqy.cloud.socket.SocketConstants;
import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.cluster.ClusterSocketServer;
import com.hqy.cloud.socket.cluster.SocketCluster;
import com.hqy.cloud.socket.cluster.support.InstanceSocketServer;
import com.hqy.cloud.socket.cluster.support.SocketClusters;
import com.hqy.cloud.util.authentication.AuthorizationService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * SocketIoSocketServer.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
@Slf4j
public class SocketIoSocketServer extends ClusterSocketServer {
    private final SocketIOServer socketIOServer;
    private final SocketIoServerModel socketIoServerModel;
    private volatile boolean destroy = false;

    public SocketIoSocketServer(ProjectInfoModel model, SocketIOServer socketIOServer, SocketIoServerModel socketIoServerModel) {
        super(model, socketIoServerModel.getPort());
        this.socketIOServer = socketIOServer;
        this.socketIoServerModel = socketIoServerModel;
    }

    @Override
    public void initialize() {
        Set<SocketIoEventListener> eventListeners = socketIoServerModel.getEventListeners();
        eventListeners.forEach(this::addEventListener);
        Boolean cluster = socketIoServerModel.getCluster();
        // 初始化元数据参数.
        getModel().setParameter(SocketConstants.ENABLED_SOCKET_SERVER_CLUSTER, cluster.toString());
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
        // 启动socketIo服务
        this.socketIOServer.start();
        // 更新cluster数据
        updateCluster();
    }

    private void updateCluster() {
        Registry registry = BeanRepository.getInstance().getBean(Registry.class);
        // 获取其他节点实例
        List<ServiceInstance> instances = registry.lookupAll(getModel());
        List<SocketServer> socketServers = new ArrayList<>(instances.size());
        instances.forEach(i -> socketServers.add(new InstanceSocketServer(i)));
        // 更新数据
        SocketCluster cluster = SocketClusters.cluster(getMetadata().getClusterType());
        cluster.init(this, socketServers);
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
