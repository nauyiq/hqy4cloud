package com.hqy.netty.websocket.handler.bind;

import com.hqy.netty.websocket.base.HandshakeData;
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


    @Override
    public void onEventException(Exception e, String eventName, Map<String, Object> args, HandshakeData handshakeData) {
        if ()
    }
}
