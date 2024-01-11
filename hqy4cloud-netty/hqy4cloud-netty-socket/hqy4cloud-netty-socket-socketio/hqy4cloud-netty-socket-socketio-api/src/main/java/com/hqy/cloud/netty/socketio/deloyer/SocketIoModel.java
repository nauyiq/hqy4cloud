package com.hqy.cloud.netty.socketio.deloyer;

import com.corundumstudio.socketio.AuthorizationListener;
import com.hqy.cloud.common.base.Parameters;
import com.hqy.cloud.netty.socketio.listener.SocketIoEventListener;

import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
public class SocketIoModel extends Parameters {

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
    private String context = "/socketio";

    /**
     * cluster?
     */
    private Boolean cluster;

    /**
     * get socketIo server lister eventListeners.
     */
    private Set<SocketIoEventListener> socketIoEventListeners;

    /**
     * AuthorizationListener.
     */
    private AuthorizationListener authorizationListener;



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

    public AuthorizationListener getAuthorizationListener() {
        return authorizationListener;
    }

    public void setAuthorizationListener(AuthorizationListener authorizationListener) {
        this.authorizationListener = authorizationListener;
    }

    public Boolean getCluster() {
        return cluster;
    }

    public void setCluster(Boolean cluster) {
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
