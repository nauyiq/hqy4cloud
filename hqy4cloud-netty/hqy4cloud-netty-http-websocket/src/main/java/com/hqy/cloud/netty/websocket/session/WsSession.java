package com.hqy.cloud.netty.websocket.session;

import com.hqy.cloud.netty.websocket.base.HandshakeData;
import com.hqy.cloud.netty.websocket.base.WsErrorReason;
import com.hqy.cloud.netty.websocket.base.enums.CloseScene;
import com.hqy.cloud.netty.websocket.base.enums.WsMessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.SocketAddress;

/**
 * 表示一次websocket请求的会话
 * websocket协议定义了不同的协议帧 (https://blog.csdn.net/xmcy001122/article/details/117226953)
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/8 15:46
 */
public interface WsSession {

    /**
     * 初始化session
     * @param context 上下文l
     */
    void initialize(ChannelHandlerContext context);

    /**
     * 接收到message处理
     * @param messageType 消息类型 二进制还是文本
     * @param message 具体的消息
     * @return
     */
    Object onMessage(WsMessageType messageType, Object message);

    /**
     * close断开处理
     * @param reason 关闭的原因
     * @param scene 关闭的场景
     */
    void onClose(WsErrorReason reason, CloseScene scene);

    /**
     * 通道打开
     */
    void onOpen();

    /**
     * 异常处理
     * @param reason 原因
     * @param t 异常
     */
    void onError(WsErrorReason reason, Throwable t);

    /**
     * 获取握手数据
     * @return 握手数据
     */
    HandshakeData getHandshakeData();

    /**
     * 初始化握手数据
     * @param request httpRequest
     * @param address address
     * @return true表示接手握手数据 false表示拒绝连接
     */
    boolean initHandshakeData(FullHttpRequest request, SocketAddress address);


    /**
     * 发消息
     * @param messageType 消息类型
     * @param message 消息内容 payload
     */
    void sendMessage(WsMessageType messageType, Object message);
}
