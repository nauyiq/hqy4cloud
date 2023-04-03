package com.hqy.mq.kafka.server;

import com.hqy.cloud.common.base.lang.exception.MessageQueueException;
import com.hqy.mq.common.bind.MessageModel;
import com.hqy.mq.common.lang.enums.MessageQueue;
import com.hqy.mq.common.server.support.AbstractProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.ListenableFutureCallback;

import static com.hqy.mq.kafka.lang.Constants.PARTITION_KEY;
import static com.hqy.mq.kafka.lang.Constants.TIMESTAMP_KEY;

/**
 * kafka消息生产者.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/6 14:22
 */
@Slf4j
public class KafkaMessageProducer extends AbstractProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaMessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        super(MessageQueue.KAFKA);
        this.kafkaTemplate = kafkaTemplate;
    }


    protected <T extends MessageModel> void doFailure(T message, Throwable ex) {
        log.error("Failed execute to send message to kafka, message: {} -> {}.", message.messageId(), message.jsonPayload(), ex);
    }

    protected void doSuccess(SendResult<String, String> result) {
        log.info("Send message to kafka success, topic:{}, partition:{}, offset:{}",
                result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
    }

    @Override
    protected <T extends MessageModel> void sendMessage(T message) throws MessageQueueException {
        String topic = message.getParameters().getTarget();
        String key = message.getParameters().getKey();
        Integer partition = message.getParameters().getInt(PARTITION_KEY);
        Long timestamp = message.getParameters().getLong(TIMESTAMP_KEY);
        kafkaTemplate.send(topic, partition, timestamp, key, message.jsonPayload()).addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(@Nullable Throwable ex) {
                doFailure(message, ex);
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                doSuccess(result);
            }
        });
    }

    public KafkaTemplate<String, String> getKafkaTemplate() {
        return kafkaTemplate;
    }
}
