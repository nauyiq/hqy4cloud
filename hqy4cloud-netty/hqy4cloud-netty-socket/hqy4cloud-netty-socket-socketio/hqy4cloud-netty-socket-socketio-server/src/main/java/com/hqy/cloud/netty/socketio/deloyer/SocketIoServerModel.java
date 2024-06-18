package com.hqy.cloud.netty.socketio.deloyer;

import com.hqy.cloud.common.base.Parameters;
import com.hqy.cloud.netty.socketio.listener.SocketIoEventListener;
import com.hqy.cloud.util.authentication.AuthorizationService;

import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
public class SocketIoServerModel extends Parameters {

    /**
     * service name.
     */
    private String serviceName;

    /**
     * socket io port.
     */
    private int port;

    /**
     * socket io context
     */
    private String context;

    /**
     * cluster?
     */
    private Boolean cluster = true;

    /**
     * get socketIo server lister eventListeners.
     */
    private Set<SocketIoEventListener> socketIoEventListeners;

    /**
     * authorizationService.
     */
    private AuthorizationService authorizationService;

    public SocketIoServerModel() {
    }

    public SocketIoServerModel(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }


    public Set<SocketIoEventListener> getSocketIoEventListeners() {
        return socketIoEventListeners;
    }

    public void setSocketIoEventListeners(Set<SocketIoEventListener> socketIoEventListeners) {
        this.socketIoEventListeners = socketIoEventListeners;
    }

    public Set<SocketIoEventListener> getEventListeners() {
        return socketIoEventListeners;
    }

    public void setEventListeners(Set<SocketIoEventListener> socketIoEventListeners) {
        this.socketIoEventListeners = socketIoEventListeners;
    }

    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public Boolean getCluster() {
        return cluster;
    }

    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;

    }
}
