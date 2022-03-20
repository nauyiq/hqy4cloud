package com.hqy.ex.listener;

import com.hqy.socketio.HandshakeData;

/**
 * socket.io 扩展的http握手安全校验机制
 * @author qiyuan.hong
 * @date 2022-03-18 00:29
 */
public class DefaultAuthorizationListener implements AuthorizationListenerAdaptor {

    @Override
    public boolean isAuthorized(HandshakeData data) {


        return false;
    }
}
