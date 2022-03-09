package com.hqy.netty.websocket.handler.bind;

import com.hqy.netty.websocket.base.HandshakeData;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 * Base callback exception listener
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/9 9:27
 */
public abstract class ExceptionListenerAdapter implements ExceptionListener {

    @Override
    public void onEventException(Exception e, String eventName, Map<String, Object> args, HandshakeData handshakeData) {

    }

    @Override
    public void onDisconnectException(Exception e, HandshakeData handshakeData) {

    }

    @Override
    public void onConnectException(Exception e, HandshakeData handshakeData) {

    }

    @Override
    public void onPingException(Exception e, HandshakeData handshakeData) {

    }

    @Override
    public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        return false;
    }
}
