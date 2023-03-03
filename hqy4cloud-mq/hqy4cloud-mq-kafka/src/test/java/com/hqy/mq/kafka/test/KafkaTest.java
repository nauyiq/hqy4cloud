package com.hqy.mq.kafka.test;

import cn.hutool.core.lang.UUID;
import com.hqy.mq.common.bind.MessageModel;
import com.hqy.mq.common.bind.MessageParams;
import com.hqy.mq.kafka.config.KafkaDefaultAutoConfiguration;
import com.hqy.mq.kafka.server.KafkaMessageProducer;
import com.hqy.cloud.util.JsonUtil;
import lombok.Data;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/9 11:07
 */

@RunWith(SpringRunner.class)
@EmbeddedKafka(topics = {"test", "test2"}, brokerPropertiesLocation = "classpath:bootstrap.yml")
@ImportAutoConfiguration({KafkaAutoConfiguration.class, KafkaDefaultAutoConfiguration.class})
@TestPropertySource("classpath:application.properties")
public class KafkaTest {

    @Autowired
    private KafkaMessageProducer producer;

    @Test
    public void sendMessage() {
        producer.send(new TestKafkaMessage());
    }

    @KafkaListener(id = "test-consumer-annotation", topicPartitions = {
        @TopicPartition(topic = "test", partitions = {"0"}),
        @TopicPartition(topic = "test2", partitions = {"0", "1"},
                partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "2"))},
        concurrency = "4", errorHandler = "defaultErrorHandler"
    )
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println(JsonUtil.toJson(record));
    }

    @Component("defaultErrorHandler")
    public static class KafkaDefaultListenerErrorHandler implements KafkaListenerErrorHandler {

        @Override
        public Object handleError(Message<?> message, ListenerExecutionFailedException exception) {
            return null;
        }

        @Override
        public Object handleError(Message<?> message, ListenerExecutionFailedException exception, Consumer<?, ?> consumer) {
            return KafkaListenerErrorHandler.super.handleError(message, exception, consumer);
        }
    }


    @Data
    private static class TestKafkaMessage implements MessageModel {

        private Long time;
        private String value;

        public TestKafkaMessage() {
            this.time = System.currentTimeMillis();
            this.value = UUID.fastUUID().toString();
        }

        @Override
        public String jsonPayload() {
            return JsonUtil.toJson(this);
        }

        @Override
        public MessageParams getParameters() {
            return new MessageParams("test2", "hongqy");
        }
    }


}
