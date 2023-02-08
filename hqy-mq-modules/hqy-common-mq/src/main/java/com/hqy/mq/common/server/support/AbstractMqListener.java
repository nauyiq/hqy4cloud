package com.hqy.mq.common.server.support;

import com.hqy.base.common.base.lang.exception.MessageQueueException;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.mq.common.bind.MessageModel;
import com.hqy.mq.common.server.Consumer;
import com.hqy.mq.common.server.MqNotifyListener;
import com.hqy.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.hqy.base.common.base.lang.exception.MessageQueueException.BUSINESS_EXCEPTION;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/6 17:52
 */
@Slf4j
public abstract class AbstractMqListener<T extends MessageModel> implements MqNotifyListener<T> {

    private final Consumer<T> consumer;

    public AbstractMqListener(Consumer<T> consumer) {
        AssertUtil.notNull(consumer, "Consumer should not be null.");
        this.consumer = consumer;
    }

    @Override
    public void notify(T message) {
        if (Objects.isNull(message)) {
            throw new MessageQueueException(MessageQueueException.EMPTY_MESSAGE_CODE, "Mq message should not be null.");
        }
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn() && log.isDebugEnabled()) {
            log.debug("Receive mq message, payload:{}.", message.jsonPayload());
        }

        try {
            this.consumer.consumption(message);
        } catch (Throwable cause) {
            this.consumer.compensate(message, cause);
            throw new MessageQueueException(BUSINESS_EXCEPTION, cause);
        }
    }
}
