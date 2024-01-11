package com.hqy.cloud.netty.socketio;

import com.corundumstudio.socketio.SocketIOServer;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
public interface SocketIoServerFactory {

    /**
     * create socketIo server
     * @return  SocketIo Server, {@link SocketIOServer}
     */
    SocketIOServer createSocketIoServer();


}
