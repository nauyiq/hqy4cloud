package com.hqy.mq.common.server;

import com.hqy.base.common.base.lang.exception.MessageQueueException;
import com.hqy.mq.common.MessageModel;

/**
 * mq消费者
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/6 17:54
 */
public interface Consumer<T extends MessageModel> {

    /**
     * 消费消息，执行业务逻辑.
     * @param message 消息
     * @throws MessageQueueException 异常
     */
    void consumption(T message) throws MessageQueueException;

    /**
     * 业务异常补偿.
     * @param message 消息
     * @param cause   异常
     */
    void compensate(T message, Throwable cause);

}
