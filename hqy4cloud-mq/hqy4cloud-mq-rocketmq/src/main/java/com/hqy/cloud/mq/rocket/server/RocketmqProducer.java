package com.hqy.cloud.mq.rocket.server;

import com.hqy.cloud.common.base.lang.exception.MessageQueueException;
import com.hqy.cloud.mq.rocket.lang.RocketmqConstants;
import com.hqy.cloud.mq.rocket.lang.RocketmqMessage;
import com.hqy.cloud.stream.api.AbstractStreamProducerTemplate;
import com.hqy.cloud.stream.api.StreamCallback;
import com.hqy.cloud.stream.api.StreamMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/29
 */
public class RocketmqProducer extends AbstractStreamProducerTemplate<SendResult> {
    private final RocketMQTemplate rocketMQTemplate;

    public RocketmqProducer(Config config, RocketMQTemplate rocketMQTemplate) {
        super(config);
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Override
    protected <K extends Comparable<K>, V> SendResult doSyncSendMessage(StreamMessage<K, V> message) {
        if (message instanceof RocketmqMessage rocketmqMessage) {
            String destination = rocketmqMessage.getDestination();
            Message<Object> payload = getMessagePayload(rocketmqMessage);
            String hashkey = rocketmqMessage.getProperty(RocketmqConstants.ORDERLY_HASH);
            return StringUtils.isBlank(hashkey) ? rocketMQTemplate.syncSend(destination, payload) : rocketMQTemplate.syncSendOrderly(destination, payload, hashkey);
        }
        throw new MessageQueueException("Un support message type by rocketmq.");
    }

    @Override
    protected <K extends Comparable<K>, V> void doAsyncSendMessage(StreamMessage<K, V> message, StreamCallback<SendResult> callback) {
        if (message instanceof RocketmqMessage rocketmqMessage) {
            String destination = rocketmqMessage.getDestination();
            Message<Object> payload = getMessagePayload(rocketmqMessage);
            String hashkey = rocketmqMessage.getProperty(RocketmqConstants.ORDERLY_HASH);

            SendCallback sendCallback = null;
            if (callback != null) {
                sendCallback = new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                         callback.onSuccess(sendResult);
                    }

                    @Override
                    public void onException(Throwable e) {
                        callback.onFailed(e);
                    }
                };
            }

            if (StringUtils.isBlank(hashkey)) {
                rocketMQTemplate.asyncSend(destination, payload, sendCallback);
            } else {
                rocketMQTemplate.asyncSendOrderly(destination, payload, hashkey, sendCallback);
            }
        }

        throw new MessageQueueException("Un support message type by rocketmq.");
    }

    private <K extends Comparable<K>, V> Message<Object> getMessagePayload(StreamMessage<K, V> message) {
        MessageBuilder<Object> builder = MessageBuilder
                .withPayload(message.gerValue());
        if (message.getId() != null) {
            builder.setHeader(RocketMQHeaders.KEYS, message.getId().get());
        }
        return builder.build();
    }



    @Override
    public String getType() {
        return RocketmqConstants.ROCKER_MQ;
    }
}
