package com.hqy.cloud.common.base.lang;

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


    String DIRECT_DEATH_QUEUE = "DIRECT_DEATH_QUEUE";

    int QUEUE_TTL = 30 * 1000;


    // =============================== EXCHANGE ==================================


    String AMQP_COLL_EXCHANGE = "COLL_FANOUT_EXCHANGE";

    String DIRECT_DEATH_EXCHANGE = "DIRECT_DEATH_EXCHANGE";

    String X_MESSAGE_TTL_KEY = "x-message-ttl";

    String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";

    String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    String DEATH_ROUTING_KEY = "death";



}
