package com.hqy.cloud.mq.kafka.lang;

import com.hqy.cloud.stream.api.MessageId;
import com.hqy.cloud.stream.common.AbstractStreamMessage;
import com.hqy.cloud.stream.core.StreamId;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/11
 */
public class KafkaStreamMessage<T> extends AbstractStreamMessage<String, T> {

    private final String topic;
    private Integer partition;

    public KafkaStreamMessage(MessageId<String> messageId, T value, String topic) {
        super(messageId, value);
        this.topic = topic;
    }

    public static <T> KafkaStreamMessage<T> of(String topic, T value) {
        return of(null, topic, value);
    }

    public static <T> KafkaStreamMessage<T> of(String messageId, String topic, T value) {
        return new KafkaStreamMessage<>(StreamId.of(messageId), value, topic);
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    @Override
    public String getTopic() {
        return topic;
    }




}
