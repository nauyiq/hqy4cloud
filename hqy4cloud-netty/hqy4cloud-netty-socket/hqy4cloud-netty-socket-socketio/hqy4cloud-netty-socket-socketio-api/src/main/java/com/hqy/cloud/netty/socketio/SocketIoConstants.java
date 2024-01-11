package com.hqy.cloud.netty.socketio;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
public interface SocketIoConstants {



    String SOCKET_SERVER_IO_ENABLED_SSL = "enabledSsl";
    boolean DEFAULT_SOCKET_SERVER_IO_ENABLED_SSL = false;

    String SOCKET_SERVER_IO_SERVER_SSL_KEYSTORE = "keystore";
    String SOCKET_SERVER_IO_SERVER_SSL_KEYSTORE_PASSWORD = "password";

    String SOCKET_IO_RANDOM_SESSION = "randomSession";
    boolean DEFAULT_SOCKET_IO_RANDOM_SESSION = true;


}
