package com.hqy.mq.common.server;

import com.hqy.base.common.base.lang.exception.MessageMqException;

/**
 * 生产者.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/3 17:02
 */
public interface Producer<T extends MqMessage> {

    /**
     * 投递消息
     * @param message mq消息
     * @throws MessageMqException 异常.
     */
    void send(T message) throws MessageMqException;

}
