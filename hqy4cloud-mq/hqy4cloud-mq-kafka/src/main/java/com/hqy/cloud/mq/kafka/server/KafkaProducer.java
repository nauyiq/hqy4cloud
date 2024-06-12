package com.hqy.cloud.mq.kafka.server;

import com.hqy.cloud.common.base.lang.exception.MessageQueueException;
import com.hqy.cloud.mq.kafka.lang.KafkaConstants;
import com.hqy.cloud.mq.kafka.lang.KafkaStreamMessage;
import com.hqy.cloud.stream.api.AbstractStreamProducerTemplate;
import com.hqy.cloud.stream.api.MessageId;
import com.hqy.cloud.stream.api.StreamCallback;
import com.hqy.cloud.stream.api.StreamMessage;
import com.hqy.cloud.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Nonnull;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/22
 */
@Slf4j
public class KafkaProducer extends AbstractStreamProducerTemplate<SendResult<String, String>> {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(Config config, KafkaTemplate<String, String> kafkaTemplate) {
        super(config);
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <K extends Comparable<K>, V> SendResult<String, String> doSyncSendMessage(StreamMessage<K, V> message) {
        if (message instanceof KafkaStreamMessage kafkaStreamMessage) {
            Message<?> convertMessage = convertMessage(kafkaStreamMessage);
            try {
                return this.kafkaTemplate.send(convertMessage).get();
            } catch (Exception e) {
                log.error("Failed execute to send kafka message, message: {}.", JsonUtil.toJson(convertMessage), e);
            }
        }
        throw new MessageQueueException("Not support stream message, please using kafka stream message.");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <K extends Comparable<K>, V> void doAsyncSendMessage(StreamMessage<K, V> message, StreamCallback<SendResult<String, String>> callback) {
        if (message instanceof KafkaStreamMessage kafkaStreamMessage) {
            Message<?> convertMessage = convertMessage(kafkaStreamMessage);
            try {
                ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(convertMessage);
                if (callback != null) {
                    future.addCallback(new ListenableFutureCallback<>() {
                        @Override
                        public void onFailure(@Nonnull Throwable ex) {
                            callback.onFailed(ex);
                        }

                        @Override
                        public void onSuccess(SendResult<String, String> result) {
                            callback.onSuccess(result);
                        }
                    });
                }
            } catch (Exception e) {
                log.error("Failed execute to send kafka message, message: {}.", JsonUtil.toJson(convertMessage), e);
            }
        }
        throw new MessageQueueException("Not support stream message, please using kafka stream message.");
    }

    public KafkaTemplate<String, String> getKafkaTemplate() {
        return kafkaTemplate;
    }

    @Override
    public String getType() {
        return KafkaConstants.KAFKA;
    }

    private <T> Message<T> convertMessage(KafkaStreamMessage<T> message) {
        MessageBuilder<T> builder = MessageBuilder.withPayload(message.gerValue());
        // 设置主题
        builder.setHeader(KafkaHeaders.TOPIC, message.getTopic());
        // 设置ID
        MessageId<String> id = message.getId();
        if (id != null) {
            builder.setHeader(MessageHeaders.ID, id.get());
        }
        // 设置分区
        Integer partition = message.getPartition();
        if (partition != null) {
            builder.setHeader(KafkaHeaders.PARTITION_ID, partition);
        }
        return builder.build();
    }

}
