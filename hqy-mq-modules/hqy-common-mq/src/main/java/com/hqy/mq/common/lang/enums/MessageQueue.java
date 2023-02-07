package com.hqy.mq.common.lang.enums;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/6 14:05
 */
public enum MessageQueue {

    /**
     * Rabbitmq.
     */
    RABBITMQ("Rabbitmq"),

    /**
     * kafka
     */
    KAFKA("Kafka"),

    /**
     * Rocketmq
     */
    ROCKETMQ("Rocketmq"),


    ;

    public final String name;

    MessageQueue(String name) {
        this.name = name;
    }
}
