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
     * 服务的消费者, 使用内部自研thrift-RPC
     */
    CONSUMER("消费者", new DeployComponent[]{ RPC_CLIENT }),

    /**
     * 服务的提供者, 使用内部自研thrift-RPC
     */
    PROVIDER("生产者", new DeployComponent[]{ RPC_CLIENT, RPC_SERVER }),

    /**
     * dubbo服务提供者
     */
    DUBBO_PROVIDER("dubbo生产者", new DeployComponent[] { DUBBO_PROVIDER_COMPONENT, DUBBO_CONSUMER_COMPONENT }),

    /**
     * dubbo服务消费者
     */
    DUBBO_CONSUMER("dubbo消费者", new DeployComponent[] { DUBBO_CONSUMER_COMPONENT, RPC_CLIENT }),

    /**
     * dubbo的socketio服务
     */
    DUBBO_SOCKETIO("dubbo长连接服务", new DeployComponent[] {DUBBO_PROVIDER_COMPONENT, DUBBO_CONSUMER_COMPONENT, SOCKETIO} ),


    /**
     * socketIo服务
     */
    SOCKETIO_SERVER("SocketIo服务", new DeployComponent[]{ RPC_CLIENT, RPC_SERVER, SOCKETIO}),

    ;

    ActuatorNode(String alias, DeployComponent[] components) {
        this.alias = alias;
        this.components = components;
    }

    public final String alias;
    public final DeployComponent[] components;


}
