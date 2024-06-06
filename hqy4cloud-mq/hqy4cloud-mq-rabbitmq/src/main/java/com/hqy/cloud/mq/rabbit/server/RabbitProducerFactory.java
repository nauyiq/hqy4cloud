package com.hqy.cloud.mq.rabbit.server;

import com.hqy.cloud.stream.api.AbstractStreamProducerFactory;
import com.hqy.cloud.stream.api.StreamProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/27
 */
@RequiredArgsConstructor
public class RabbitProducerFactory extends AbstractStreamProducerFactory<Void> {
    private final RabbitTemplate rabbitTemplate;

    @Override
    protected StreamProducer<Void> doCreate(StreamProducer.Config config) {
        return new RabbitmqProducer(config, rabbitTemplate);
    }
}
