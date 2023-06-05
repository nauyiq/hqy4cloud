package com.hqy.cloud.common.base.lang;

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
    CONSUMER,

    /**
     * 服务的提供者
     */
    PROVIDER,

    /**
     * 两者都是
     */
    BOTH

    ;

    public boolean isProvider() {
        return this == PROVIDER || this == BOTH;
    }

}
