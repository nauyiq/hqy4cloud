package com.hqy.mq.rocketmq.server;

import com.hqy.base.common.base.lang.exception.MessageQueueException;
import com.hqy.mq.common.MessageModel;
import com.hqy.mq.common.lang.enums.MessageQueue;
import com.hqy.mq.common.lang.enums.MessageType;
import com.hqy.mq.common.server.support.AbstractProducer;
import com.hqy.mq.rocketmq.lang.RocketmqConstants;
import com.hqy.mq.rocketmq.lang.RocketmqMessageModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/7 16:02
 */
@Slf4j
public abstract class RocketmqMessageProducer extends AbstractProducer {
    private final RocketMQTemplate rocketMQTemplate;
    public RocketmqMessageProducer(RocketMQTemplate rocketMQTemplate) {
        super(MessageQueue.ROCKETMQ);
        this.rocketMQTemplate = rocketMQTemplate;
    }

    protected abstract <T extends MessageModel> RocketmqMessageModel buildMessage(T message);

    protected void doSuccess(RocketmqMessageModel model) {
        if (log.isDebugEnabled()) {
            log.debug("Send message to rocketmq success, payload: {}.", model.payload());
        }
    }

    protected void doFailure(RocketmqMessageModel model, SendStatus status) {
        log.error("Failed execute to send message to rocketmq, status: {}, message: {}.", status, model.payload());
    }


    @Override
    protected <T extends MessageModel> void sendMessage(T message) throws MessageQueueException {
        RocketmqMessageModel model = buildMessage(message);
        if (Objects.isNull(model) ||
                StringUtils.isAnyBlank(model.topic(), model.payload())) {
            throw new MessageQueueException(MessageQueueException.EMPTY_MESSAGE_CODE, "Rabbitmq message should not be null.");
        }

        String topic = model.topic();
        String payload = model.payload();
        Message<String> rocketMessage = MessageBuilder.withPayload(payload).setHeader(RocketmqConstants.ID_KEYS, model.messageId()).build();


        //消息类型
        MessageType messageType = model.messageType();
        switch (messageType) {
            case ONEWAY:
                rocketMQTemplate.sendOneWay(topic, rocketMessage);
                break;
            case SYNC:
                SendResult sendResult = rocketMQTemplate.syncSend(topic, payload);
                SendStatus sendStatus = sendResult.getSendStatus();
                if (sendStatus.equals(SendStatus.SEND_OK)) {
                    doSuccess(model);
                } else {
                    doFailure(model, sendStatus);
                }
                break;
            default:
                rocketMQTemplate.asyncSend(topic, rocketMessage, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        doSuccess(model);
                    }

                    @Override
                    public void onException(Throwable e) {
                        doFailure(model, null);
                    }
                });

        }



    }



}
