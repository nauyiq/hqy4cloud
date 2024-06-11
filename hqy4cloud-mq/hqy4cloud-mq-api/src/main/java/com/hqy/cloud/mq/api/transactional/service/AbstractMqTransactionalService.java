package com.hqy.cloud.mq.api.transactional.service;

import com.hqy.cloud.mq.api.transactional.common.MqTransactionalException;
import com.hqy.cloud.stream.api.StreamProducer;
import com.hqy.cloud.stream.core.StreamResult;
import com.hqy.cloud.util.concurrent.async.tool.worker.ResultState;
import org.apache.commons.lang3.StringUtils;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/7
 */
public abstract class AbstractMqTransactionalService implements MqTransactionalService {
    private final MqMessageOperations mqMessageOperations;
    private final StreamProducer<?> producer;

    protected AbstractMqTransactionalService(MqMessageOperations mqMessageOperations, StreamProducer<?> producer) {
        this.mqMessageOperations = mqMessageOperations;
        this.producer = producer;
    }

    @Override
    public <T> void saveAndSendLocalMessage(LocalTransactionalMessage<T> message) throws MqTransactionalException {
        // 校验事务消息.
        if (message == null || StringUtils.isAnyBlank(message.getMessageId(), message.getTopic())) {
            throw new MqTransactionalException(MqTransactionalException.INVALID_MESSAGE ,"Invalid mq transactional message, please check again.");
        }

        // 保存事务消息.
        if (!mqMessageOperations.saveMqMessage(message)) {
            throw new MqTransactionalException(MqTransactionalException.SAVING_MESSAGE_ERROR, "Failed execute to save mq transactional message.");
        }

        // 发送事务消息到mq
        StreamResult<?> streamResult = producer.syncSend(message);
        if (streamResult == null || streamResult.getState() != ResultState.SUCCESS) {
            throw new MqTransactionalException(MqTransactionalException.SEND_MESSAGE_ERROR, "Failed execute to send mq message.");
        }
    }
}
