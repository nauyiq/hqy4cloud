package com.hqy.cloud.netty.websocket.exception;

import com.hqy.cloud.common.base.lang.exception.PublishedException;
import com.hqy.cloud.netty.websocket.base.HandshakeData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.unix.Errors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/9 9:29
 */
public class DefaultExceptionListener extends ExceptionListenerAdapter{

    private static final Logger log = LoggerFactory.getLogger(DefaultExceptionListener.class);

    int point = 0;


    @Override
    public void onEventException(Exception e, String eventName, Map<String, Object> args, HandshakeData handshakeData) {
        if (e instanceof PublishedException) {
            return;
        }
        if (isIgnoredException(e)) {
            return;
        }
        //TODO 异常处理
    }

    @Override
    public void onDisconnectException(Exception e, HandshakeData handshakeData) {
        if(e instanceof PublishedException) {
            return;
        }
        log.error("@@@ onDisconnectException");
        log.error(e.getMessage(), e);
    }

    @Override
    public void onConnectException(Exception e, HandshakeData handshakeData) {
        if(e instanceof PublishedException) {
            return;
        }
        log.error("@@@  onConnectException");
        log.error(e.getMessage(), e);
    }

    @Override
    public void onPingException(Exception e, HandshakeData handshakeData) {
        if(e instanceof PublishedException) {
            return;
        }
        log.error("@@@ onPingException");
        log.error(e.getMessage(), e);
    }


    @Override
    public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        if (e instanceof PublishedException) {
            return true;
        }
        if (isIgnoredException(e)) {
            return true;
        }
        //TODO 异常处理
        return true;
    }

    private boolean isIgnoredException(Throwable e) {
        if(e instanceof java.io.IOException && "Connection reset by peer".equals(e.getMessage())) {
            return true;
        }
        if(e instanceof Errors.NativeIoException) {
            point++;
            //NativeIoException 采样 取二十分之一即可
            if(point % 20  == 1) {
                return false;
            }else {
                return true;
            }
        }
        if(e instanceof io.netty.handler.codec.http.websocketx.CorruptedWebSocketFrameException) {
            log.warn(e.getClass() + ":" + e.getMessage());
            return true;
        }
        return false;
    }
}
