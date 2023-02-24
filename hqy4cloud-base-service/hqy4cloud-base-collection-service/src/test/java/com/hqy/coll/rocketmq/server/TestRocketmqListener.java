package com.hqy.coll.rocketmq.server;

import com.hqy.mq.common.server.Consumer;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/13 13:42
 */
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "test-consumer-group", topic = "hahahahaha", selectorExpression = "rocketmq-producer-group-test")
public class TestRocketmqListener implements RocketMQListener<TestRocketmqMessage> {

    private final Consumer<TestRocketmqMessage> consumer;

    @Override
    public void onMessage(TestRocketmqMessage message) {
        consumer.consumption(message);
    }
}
