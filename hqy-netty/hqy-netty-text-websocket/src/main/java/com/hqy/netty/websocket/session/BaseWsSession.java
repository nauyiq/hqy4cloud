package com.hqy.netty.websocket.session;

import com.hqy.netty.websocket.base.HandshakeData;
import io.netty.channel.ChannelHandlerContext;

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
     * 上下文
     */
    protected ChannelHandlerContext context;


    public BaseWsSession() {
    }


    @Override
    public HandshakeData getHandshakeData() {
        return handshakeData;
    }




}
