package com.hqy.netty.websocket.session;

import com.hqy.netty.http.FullHttpRequestProcessor;
import com.hqy.netty.websocket.base.HandshakeData;
import com.hqy.netty.websocket.base.enums.WsMessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.SocketAddress;

/**
 * websocket连接会话基础类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/8 16:49
 */
public abstract class BaseWsSession implements WsSession {

    /**
     * 握手数据
     */
    private HandshakeData handshakeData;

    /**
     * 客户端channel上下文
     */
    protected ChannelHandlerContext context;


    public BaseWsSession() {
    }


    @Override
    public void initialize(ChannelHandlerContext context) {
        this.context = context;
    }

    @Override
    public HandshakeData getHandshakeData() {
        return handshakeData;
    }


    @Override
    public void sendMessage(WsMessageType messageType, Object message) {

    }

    @Override
    public boolean initHandshakeData(FullHttpRequest httpRequest, SocketAddress address) {
        try {
            FullHttpRequestProcessor processor = new FullHttpRequestProcessor(httpRequest);
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 是否连接
     * @return true 已连接 false 未连接
     */
    public boolean isConnect() {
        return context.channel().isActive() && context.channel().isOpen();
    }
}
