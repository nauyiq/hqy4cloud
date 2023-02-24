package com.hqy.socketio;

import com.hqy.socketio.support.EventListener;

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
    default int clusterNode() {
        return 1;
    }

    /**
     * enable cluster socket.io
     * @return default false
     */
    default boolean enableMultiNodes() {
        return false;
    }

    /**
     * this socket.io server hash
     * @return hash
      */
    default int thisHash() {
        return 0;
    }

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
     * AuthorizationListener.
     * @return AuthorizationListener.
     */
    AuthorizationListener authorizationListener();



}
