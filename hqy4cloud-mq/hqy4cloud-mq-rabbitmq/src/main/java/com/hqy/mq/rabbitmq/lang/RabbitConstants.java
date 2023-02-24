package com.hqy.mq.rabbitmq.lang;

/**
 * rabbitmq 常量类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/31 10:28
 */
public interface RabbitConstants {

    /**
     * 设置队列的TTL KEY
     */
    String TTL = "x-message-ttl";

    /**
     * 死信队列-交换机 KEY
     */
   String DEAD_EXCHANGE = "x-dead-letter-exchange";

    /**
     * 死信队列-路由key KEY
     */
    String DEAD_EXCHANGE_ROOTING_KEY = "x-dead-letter-routing-key";

    /**
     * 设置队列优先级
     */
    String QUEUE_MAX_PRIORITY_KEY = "x-max-priority";

}
