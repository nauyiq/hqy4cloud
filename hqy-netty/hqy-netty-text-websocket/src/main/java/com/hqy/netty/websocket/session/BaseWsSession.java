package com.hqy.netty.websocket.session;

import com.hqy.base.common.base.lang.BaseStringConstants;
import com.hqy.netty.http.FullHttpRequestProcessor;
import com.hqy.netty.websocket.base.HandshakeData;
import com.hqy.netty.websocket.base.enums.WsMessageType;
import com.hqy.netty.websocket.handler.WebsocketHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * websocket连接会话基础类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/8 16:49
 */
public abstract class BaseWsSession implements WsSession {

    private static final Logger log = LoggerFactory.getLogger(BaseWsSession.class);

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
            //获取客户端连接ip
            String remoteIp = processor.getRemoteIp();
            if (StringUtils.isBlank(remoteIp)) {
                if (address instanceof InetSocketAddress) {
                    InetSocketAddress inetSocketAddress = (InetSocketAddress) address;
                    remoteIp = inetSocketAddress.getAddress().toString();
                } else {
                    remoteIp = address.toString();
                }
                if (remoteIp.startsWith(BaseStringConstants.Symbol.INCLINED_ROD)) {
                    //去掉开头的 ‘/'
                    remoteIp = remoteIp.substring(1);
                }
            }

            handshakeData = new HandshakeData();
            handshakeData.setRemoteIp(remoteIp);
            handshakeData.setParams(processor.getParams());

            if(handshakeData.getUid() == null ) {
                WebsocketHandler.debugPrint("initHandShakeData Error, 无法获得uid");
                return false;
            }
            if(StringUtils.isBlank(handshakeData.getRemoteIp())) {
                WebsocketHandler.debugPrint("initHandShakeData Error, 无法获得ip");
                return false;
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }


    /**
     * 子类实现握手检查..
     * 检查 handShakeData 中的数据是否ok
     * @return
     */
    protected abstract boolean checkHandShake();


    /**
     * 是否连接
     * @return true 已连接 false 未连接
     */
    public boolean isConnect() {
        return context.channel().isActive() && context.channel().isOpen();
    }
}
