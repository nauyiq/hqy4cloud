package com.hqy.mq.kafka.config;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.canal.core.CanalGlue;
import com.hqy.cloud.canal.core.processor.BaseCanalBinlogEventProcessor;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.rpc.core.Environment;
import com.hqy.mq.kafka.canal.CanalListener;
import com.hqy.mq.kafka.canal.DefaultCanalListener;
import com.hqy.mq.kafka.lang.KafkaConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

/**
 * 注册KAFKA-CANAL监听器
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
    @ConditionalOnBean(value = BaseCanalBinlogEventProcessor.class)
    public KafkaMessageListenerContainer<String, String> kafkaMessageListenerContainer() {
        // 消费主题
        String topic;
        if (CommonSwitcher.ENABLE_CANAL_ENV_ISO.isOn()) {
            topic = environment.isDevEnvironment() ? KafkaConstants.DEV_CANAL_KAFKA_TOPIC : KafkaConstants.TEST_CANAL_KAFKA_TOPIC;
        } else {
            topic = KafkaConstants.DEFAULT_CANAL_KAFKA_TOPIC;
        }
        // 消费者组
        String consumerGroup = application.concat(StrUtil.COLON).concat(environment.getEnvironment());
        // 创建canal监听器
        CanalListener canalListener = new DefaultCanalListener(canalGlue);
        ContainerProperties properties = new ContainerProperties(topic);
        properties.setGroupId(consumerGroup);
        properties.setMessageListener(canalListener);
        properties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return new KafkaMessageListenerContainer<>(kafkaConsumerFactory, properties);
    }


}
