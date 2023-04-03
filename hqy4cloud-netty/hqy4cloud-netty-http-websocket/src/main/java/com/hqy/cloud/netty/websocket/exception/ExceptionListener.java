package com.hqy.cloud.netty.websocket.exception;

import com.hqy.cloud.netty.websocket.base.HandshakeData;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/9 9:21
 */
public interface ExceptionListener {

    /**
     * 发生事件异常
     * @param e 什么异常
     * @param eventName 事件名
     * @param args 参数
     * @param handshakeData 握手数据
     */
    void onEventException(Exception e, String eventName, Map<String, Object> args, HandshakeData handshakeData);


    /**
     * 断开连接时异常
     * @param e 什么异常
     * @param handshakeData 握手数据
     */
    void onDisconnectException(Exception e, HandshakeData handshakeData);


    /**
     * 连接时异常
     * @param e 什么异常
     * @param handshakeData 握手数据
     */
    void onConnectException(Exception e, HandshakeData handshakeData);


    /**
     * 客户端发ping帧时异常
     * @param e 什么异常
     * @param handshakeData 握手数据
     */
    void onPingException(Exception e, HandshakeData handshakeData);


    /**
     * 捕获异常
     * @param ctx 上下文
     * @param e 异常
     * @return 结果
     * @throws Exception
     */
    boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception;

}
