package com.hqy.cloud.common.base.lang;

import static com.hqy.cloud.common.base.lang.DeployComponent.*;

/**
 * 服务节点枚举 即服务是消费者还是提供者
 * 一般来说服务的提供者是暴露服务给其他服务调用 当然服务的提供者或消费者都是相对而言.
 * 进行系统设计时 推荐正常的http服务 无需暴露rpc的设计成消费者
 * 需要提供rpc调用的服务设计成提供者.
 * @author qiyaun.hong
 * @date 2021-10-08 19:13
 */
public enum ActuatorNode {


    /**
     * 服务的消费者
     */
    CONSUMER("消费者", new DeployComponent[]{ RPC_CLIENT }),

    /**
     * 服务的提供者
     */
    PROVIDER("生产者", new DeployComponent[]{ RPC_CLIENT, RPC_SERVER }),


    /**
     * socketIo服务
     */
    SOCKET_IO_SERVER("SocketIo服务", new DeployComponent[]{ RPC_CLIENT, RPC_SERVER, SOCKET_IO }),

    ;

    ActuatorNode(String alias, DeployComponent[] components) {
        this.alias = alias;
        this.components = components;
    }

    public final String alias;
    public final DeployComponent[] components;

    public boolean isProvider() {
        return this == PROVIDER || this == SOCKET_IO_SERVER;
    }

}
