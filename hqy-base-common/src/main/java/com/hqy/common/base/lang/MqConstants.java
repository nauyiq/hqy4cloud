package com.hqy.common.base.lang;

/**
 * @author qy
 * @create 2021/8/19 23:23
 */
public interface MqConstants {

    // =============================== QUEUE ==================================

    /**
     * 网关gateway专用队列
     */
    String AMQP_GATEWAY_QUEUE = "GATEWAY_QUEUE";

    // =============================== EXCHANGE ==================================


    String AMQP_COLL_EXCHANGE = "COLL_FANOUT_EXCHANGE";
}
