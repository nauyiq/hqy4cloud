package com.hqy.mq.common.server;

import com.hqy.base.common.base.lang.exception.MessageQueueException;
import com.hqy.mq.common.bind.MessageModel;

/**
 * 生产者.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/3 17:02
 */
public interface Producer {

    /**
     * 投递消息
     * @param message mq消息
     * @throws MessageQueueException 异常.
     */
    <T extends MessageModel> void send(T message) throws MessageQueueException;

}
