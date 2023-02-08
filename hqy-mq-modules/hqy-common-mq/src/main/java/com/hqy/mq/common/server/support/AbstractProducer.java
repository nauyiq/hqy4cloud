package com.hqy.mq.common.server.support;

import com.hqy.base.common.base.lang.exception.MessageQueueException;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.mq.common.lang.enums.MessageQueue;
import com.hqy.mq.common.bind.MessageModel;
import com.hqy.mq.common.server.Producer;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/6 14:00
 */
@Slf4j
public abstract class AbstractProducer implements Producer {

    private final MessageQueue mq;

    public AbstractProducer(MessageQueue mq) {
        this.mq = mq;
    }

    @Override
     public <T extends MessageModel> void send(T message) throws MessageQueueException {
        if (Objects.isNull(message) || Objects.isNull(message.getParameters())) {
            throw new MessageQueueException(MessageQueueException.EMPTY_MESSAGE_CODE, "Mq message should not be null.");
        }
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn() && log.isDebugEnabled()) {
            log.debug("Send message to {}, message: {}.", mq.name, message.jsonPayload());
        }
        sendMessage(message);
    }

    /**
     * 发消息逻辑由具体的消息队列模块处理。
     * @param message 消息j
     * @throws MessageQueueException 异常.
     */
    protected abstract <T extends MessageModel> void sendMessage(T message) throws MessageQueueException;

}
