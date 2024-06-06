package com.hqy.cloud.mq.kafka.server;

import com.hqy.cloud.stream.api.AbstractStreamProducerFactory;
import com.hqy.cloud.stream.api.StreamProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/22
 */
public class KafkaProducerFactory extends AbstractStreamProducerFactory<SendResult<String, String>> {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerFactory(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    protected StreamProducer<SendResult<String, String>> doCreate(StreamProducer.Config config) {
        return new KafkaProducer(config, kafkaTemplate);
    }
}
