package com.hqy.cloud.stream.core;

/**
 * 通用的流消息监听器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/10
 */
public @interface StreamConsumerListenerConfig {

    /**
     * 消费的主题, 在一个JVM应用中应该是唯一的
     * @return  stream 消息主题. 订阅的主题
     */
    String topic();

    /**
     * 消费者监听器名， 用于区分应用
     * @return 消费者名
     */
    String name();

    /**
     *
     * @return
     */
    String group();

    long requestTimeoutSecond();

    long intervalSecond();

    long timeoutSecond();


}
