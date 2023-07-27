package com.hqy.cloud.socketio.starter.core;

import com.corundumstudio.socketio.AuthorizationListener;
import com.hqy.cloud.socketio.starter.core.support.EventListener;

import java.util.Set;

/**
 * SocketIoServerStarter.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/23 14:23
 */
public interface SocketIoServerStarter {

    /**
     * service name.
     * @return serviceName.
     */
    String serviceName();

    /**
     * socket.io node counter.
     * @return socket.io node counter
     */
    int clusterNode();

    /**
     * enable cluster socket.io
     * @return default false
     */
    boolean isCluster();

    /**
     * this socket.io server hash
     * @return hash
      */
    int clusterHash();

    /**
     * get eventListeners.
     * @return set for EventListener.
     */
    Set<EventListener> eventListeners();

    /**
     * server port.
     * @return socketIo server port.
     */
    int serverPort();

    /**
     * socketIo namespace contextPath.
     * @return contextPath
     */
    String contextPath();

    /**
     * socketIo authorizationSecret.
     * @return authorizationSecret
     */
    String authorizationSecret();

    /**
     * AuthorizationListener.
     * @return AuthorizationListener.
     */
    AuthorizationListener authorizationListener();



}
