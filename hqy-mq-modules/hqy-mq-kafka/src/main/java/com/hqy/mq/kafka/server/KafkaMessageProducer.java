package com.hqy.mq.kafka.server;

import com.hqy.base.common.base.lang.exception.MessageQueueException;
import com.hqy.mq.common.lang.enums.MessageQueue;
import com.hqy.mq.common.MessageModel;
import com.hqy.mq.common.server.support.AbstractProducer;
import com.hqy.mq.kafka.lang.KafkaMessageModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Objects;

/**
 * kafka消息生产者.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/6 14:22
 */
@Slf4j
public abstract class KafkaMessageProducer extends AbstractProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaMessageProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(MessageQueue.KAFKA);
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 由具体业务构建kafka请求的消息
     * @param messageModel kafka消息
     * @return KafkaMessage.
     */
    protected abstract <T extends MessageModel> KafkaMessageModel buildMessage(T messageModel);

    protected void doFailure(KafkaMessageModel message, Throwable ex) {
        log.error("Failed execute to send message to kafka, message: {}.", message.payload(), ex);
    }

    protected void doSuccess(SendResult<String, Object> result) {
        log.info("Send message to kafka success, topic:{}, partition:{}, offset:{}",
                result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
    }

    @Override
    protected <T extends MessageModel> void sendMessage(T message) throws MessageQueueException {
        KafkaMessageModel kafkaMessage = buildMessage(message);
        if (Objects.isNull(kafkaMessage) || StringUtils.isBlank(kafkaMessage.topic())) {
            throw new MessageQueueException(MessageQueueException.EMPTY_MESSAGE_CODE, "Kafka message should not be null.");
        }
        String topic = kafkaMessage.topic();
        String key = kafkaMessage.key();
        Integer partition = kafkaMessage.partition();
        Long timestamp = kafkaMessage.timestamp();
        kafkaTemplate.send(topic, partition, timestamp, key, kafkaMessage.payload()).addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(@Nullable Throwable ex) {
                doFailure(kafkaMessage, ex);
            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                doSuccess(result);
            }
        });
    }
}
