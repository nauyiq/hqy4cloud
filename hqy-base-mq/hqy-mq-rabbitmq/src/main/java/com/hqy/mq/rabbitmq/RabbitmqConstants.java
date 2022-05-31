package com.hqy.mq.rabbitmq;

/**
 * rabbitmq 常量类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/31 10:28
 */
public final class RabbitmqConstants {

    private RabbitmqConstants() {}

    /**
     * 设置队列的TTL KEY
     */
    public static final String TTL = "x-message-ttl";

    /**
     * 死信队列-交换机 KEY
     */
    public static final String DEAD_EXCHANGE = "x-dead-letter-exchange";

    /**
     * 死信队列-路由key KEY
     */
    public static final String DEAD_EXCHANGE_ROOTING_KEY = "x-dead-letter-routing-key";




}
