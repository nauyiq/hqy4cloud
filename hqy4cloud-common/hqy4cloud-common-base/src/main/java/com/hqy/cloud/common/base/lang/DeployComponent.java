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
    SOCKETIO(Constants.SOCKETIO_COMPONENT),

    /**
     * RpcClient
     */
    RPC_CLIENT(Constants.RPC_CLIENT_COMPONENT),

    /**
     * RpcServer
     */
    RPC_SERVER(Constants.RPC_SERVER_COMPONENT),

    /**
     * dubbo服务
     */
    DUBBO_COMPONENT(Constants.DUBBO_COMPONENT),
    
    ;
    
    public final String name;


    DeployComponent(String name) {
        this.name = name;
    }

    public static class Constants {
        public static final String RPC_SERVER_COMPONENT = "rpc-server";
        public static final String RPC_CLIENT_COMPONENT = "rpc-client";
        public static final String SOCKETIO_COMPONENT = "socketio";
        public static final String DUBBO_COMPONENT = "dubbo";
    }

}
