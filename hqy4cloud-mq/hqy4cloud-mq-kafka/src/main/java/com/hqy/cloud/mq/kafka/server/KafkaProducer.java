package com.hqy.cloud.mq.kafka.server;

import com.hqy.cloud.common.base.lang.exception.MessageQueueException;
import com.hqy.cloud.mq.kafka.lang.KafkaConstants;
import com.hqy.cloud.stream.api.AbstractStreamProducerTemplate;
import com.hqy.cloud.stream.api.StreamCallback;
import com.hqy.cloud.stream.api.StreamMessage;
import com.hqy.cloud.stream.core.CompletableFutureResult;
import com.hqy.cloud.util.JsonUtil;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Nonnull;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/22
 */
public class KafkaProducer extends AbstractStreamProducerTemplate<SendResult<String, String>> {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(Config config, KafkaTemplate<String, String> kafkaTemplate) {
        super(config);
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    protected <K extends Comparable<K>, V> SendResult<String, String> doSyncSendMessage(StreamMessage<K, V> message) {
        V v = message.gerValue();
        String data = JsonUtil.toJson(v);
        try {
            return this.kafkaTemplate.send(message.getTopic(), data).get();
        } catch (Throwable e) {
            throw new MessageQueueException(e);
        }
    }

    @Override
    protected <K extends Comparable<K>, V> void doAsyncSendMessage(StreamMessage<K, V> message, StreamCallback<SendResult<String, String>> callback) {
        V v = message.gerValue();
        String data = JsonUtil.toJson(v);
        ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(message.getTopic(), data);
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
    }

    public KafkaTemplate<String, String> getKafkaTemplate() {
        return kafkaTemplate;
    }

    @Override
    public String getType() {
        return KafkaConstants.KAFKA;
    }
}
