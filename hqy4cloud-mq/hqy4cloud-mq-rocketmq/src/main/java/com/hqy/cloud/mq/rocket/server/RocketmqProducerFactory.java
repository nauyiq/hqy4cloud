package com.hqy.cloud.mq.rocket.server;

import com.hqy.cloud.stream.api.AbstractStreamProducerFactory;
import com.hqy.cloud.stream.api.StreamProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/7
 */
public class RocketmqProducerFactory extends AbstractStreamProducerFactory<SendResult> {
    private final RocketMQTemplate rocketMQTemplate;

    public RocketmqProducerFactory(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Override
    protected StreamProducer<SendResult> doCreate(StreamProducer.Config config) {
        return new RocketmqProducer(config, rocketMQTemplate);
    }
}
