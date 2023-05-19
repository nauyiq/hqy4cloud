package com.hqy.mq.kafka.config;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.canal.core.CanalGlue;
import com.hqy.cloud.rpc.core.Environment;
import com.hqy.mq.kafka.canal.CanalListener;
import com.hqy.mq.kafka.canal.DefaultCanalListener;
import com.hqy.mq.kafka.lang.KafkaConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

/**
 * 由于用了一个canal去监听了开发库和测试库的binlog
 * 注册Canal listener要根据环境注册一下对应的bean.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/18 15:26
 */
@Configuration
@RequiredArgsConstructor
public class CanalListenerConfiguration {

    @Value("${spring.application.name}")
    private String application;
    private final DefaultKafkaConsumerFactory<String, String> kafkaConsumerFactory;
    private final Environment environment;
    private final CanalGlue canalGlue;

    @Bean
    public KafkaMessageListenerContainer<String, String> kafkaMessageListenerContainer() {
        String topic = environment.isDevEnvironment() ? KafkaConstants.DEV_CANAL_KAFKA_TOPIC : KafkaConstants.TEST_CANAL_KAFKA_TOPIC;
        String consumerGroup = application + StrUtil.DASHED + environment.getEnvironment();
        CanalListener canalListener =  new DefaultCanalListener(canalGlue);
        ContainerProperties properties = new ContainerProperties(topic);
        properties.setGroupId(consumerGroup);
        properties.setMessageListener(canalListener);
        properties.setAckMode(ContainerProperties.AckMode.MANUAL);
        return new KafkaMessageListenerContainer(kafkaConsumerFactory, properties);
    }


}
