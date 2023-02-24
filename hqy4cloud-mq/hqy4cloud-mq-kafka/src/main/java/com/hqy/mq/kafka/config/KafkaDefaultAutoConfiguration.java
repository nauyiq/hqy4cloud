package com.hqy.mq.kafka.config;

import com.hqy.mq.kafka.server.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.LoggingProducerListener;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;

/**
 * kafka配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/9 10:14
 */
@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaDefaultAutoConfiguration {

    private final KafkaProperties kafkaProperties;

    @ConditionalOnMissingBean
    @Bean(name="defaultKafkaTemplate")
    public KafkaTemplate<String, String> kafkaTemplate(@Qualifier("defaultKafkaProducerFactory") ProducerFactory<String, String> kafkaProducerFactory,
                                                       ProducerListener<String, String> kafkaProducerListener,
                                                       ObjectProvider<RecordMessageConverter> messageConverter) {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory);
        messageConverter.ifUnique(kafkaTemplate::setMessageConverter);
        kafkaTemplate.setProducerListener(kafkaProducerListener);
        kafkaTemplate.setDefaultTopic(kafkaProperties.getTemplate().getDefaultTopic());
        return kafkaTemplate;
    }

    @Bean
    public ProducerListener<String, String> kafkaProducerListener() {
        return new LoggingProducerListener<>();
    }

    @ConditionalOnMissingBean
    @Bean(name="defaultKafkaProducerFactory")
    public ProducerFactory<String, String> kafkaProducerFactory() {
        DefaultKafkaProducerFactory<String, String> factory = new DefaultKafkaProducerFactory<>(
                kafkaProperties.buildProducerProperties());
        String transactionIdPrefix = kafkaProperties.getProducer().getTransactionIdPrefix();
        if (transactionIdPrefix != null) {
            factory.setTransactionIdPrefix(transactionIdPrefix);
        }
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaMessageProducer kafkaMessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        return new KafkaMessageProducer(kafkaTemplate);
    }







}
