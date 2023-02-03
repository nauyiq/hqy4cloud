package com.hqy.mq.rabbitmq.listener.strategy;

import com.hqy.mq.common.listener.payload.MessagePayload;

/**
 * Listen for rabbitmq strategy.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/6 17:17
 */
public interface ListenerStrategy<T extends MessagePayload> {

    /**
     * Listen for the execution action of the message.
     * @param payload    receive message payload.
     * @throws RuntimeException not catch RuntimeException.
     */
    void action(T payload) throws RuntimeException;


    /**
     * Compensation when an exception occurs.
     * @param payload     receive message payload.
     * @throws RuntimeException  not catch RuntimeException.
     */
    void compensate(T payload) throws  RuntimeException;


}
