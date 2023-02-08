package com.hqy.mq.rocketmq.server;

import com.hqy.base.common.base.lang.exception.MessageQueueException;
import com.hqy.mq.common.bind.MessageModel;
import com.hqy.mq.common.lang.enums.MessageQueue;
import com.hqy.mq.common.lang.enums.MessageType;
import com.hqy.mq.common.server.support.AbstractProducer;
import com.hqy.mq.rocketmq.lang.RocketmqConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static com.hqy.base.common.base.lang.exception.MessageQueueException.FAILED_SEND_MESSAGE;

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


    protected <T extends MessageModel> void doSuccess(T message) {
        if (log.isDebugEnabled()) {
            log.debug("Send message to rocketmq success, message: {} -> {}.", message.messageId(), message.jsonPayload());
        }
    }

    protected <T extends MessageModel> void doFailure(T message, Throwable ex) {
        log.error("Failed execute to send message to rocketmq, message: {}.", message.jsonPayload(), ex);
    }


    @Override
    protected <T extends MessageModel> void sendMessage(T message) throws MessageQueueException {
        //主题
        String topic = message.getParameters().getTarget();
        //消息内容json
        String payload = message.jsonPayload();
        if (StringUtils.isAnyBlank(topic, payload)) {
            throw new MessageQueueException(MessageQueueException.EMPTY_MESSAGE_PARAMS, "Rabbitmq message params should not be empty.");
        }

        //构建Rocketmq消息对象
        Message<String> rocketMessage = MessageBuilder.withPayload(payload).setHeader(RocketmqConstants.ID_KEYS, message.messageId()).build();
        //消息类型
        MessageType messageType = message.messageType();
        switch (messageType) {
            case ONEWAY:
                rocketMQTemplate.sendOneWay(topic, rocketMessage);
                break;
            case SYNC:
                SendResult sendResult = rocketMQTemplate.syncSend(topic, payload);
                SendStatus sendStatus = sendResult.getSendStatus();
                if (sendStatus.equals(SendStatus.SEND_OK)) {
                    doSuccess(message);
                } else {
                    doFailure(message, new MessageQueueException(FAILED_SEND_MESSAGE, "Failed execute to sync send message to rocketmq."));
                }
                break;
            default:
                rocketMQTemplate.asyncSend(topic, rocketMessage, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        doSuccess(message);
                    }

                    @Override
                    public void onException(Throwable e) {
                        doFailure(message, e);
                    }
                });

        }

    }

    public RocketMQTemplate getRocketMQTemplate() {
        return rocketMQTemplate;
    }
}
