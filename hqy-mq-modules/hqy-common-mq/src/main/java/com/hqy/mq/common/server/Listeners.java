package com.hqy.mq.common.server;

/**
 * 监听器.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/3 17:00
 */
public interface Listeners<T extends MqMessage> {

    /**
     * 获取当前消息监听对应的消费者
     * @return {@link Consumer}
     */
    Consumer<T> getConsumer();

}
