package com.hqy.cloud.common.base.lang;

/**
 * DeployComponent.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/5
 */
public enum DeployComponent {

    /**
     * socketIO
     */
    SOCKET_IO(Constants.SOCKET_IO_COMPONENT),

    /**
     * RpcClient
     */
    RPC_CLIENT(Constants.RPC_CLIENT_COMPONENT),

    /**
     * RpcServer
     */
    RPC_SERVER(Constants.RPC_SERVER_COMPONENT),

    
    ;
    
    public final String name;


    DeployComponent(String name) {
        this.name = name;
    }

    public static class Constants {
        public static final String RPC_SERVER_COMPONENT = "rpc-sever-component";
        public static final String RPC_CLIENT_COMPONENT = "rpc-client-component";
        public static final String SOCKET_IO_COMPONENT = "socket-io-component";
    }

}
