package com.hqy.cloud.mq.kafka.config;

import com.hqy.cloud.mq.api.transactional.service.MqMessageOperations;
import com.hqy.cloud.mq.api.transactional.service.MqTransactionalService;
import com.hqy.cloud.mq.kafka.server.KafkaTransactionalService;
import com.hqy.cloud.stream.api.StreamProducer;
import com.hqy.cloud.mq.kafka.server.KafkaProducer;
import com.hqy.cloud.mq.kafka.server.KafkaProducerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.LoggingProducerListener;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.transaction.KafkaTransactionManager;

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


    /*producer configuration*/

    @Bean
    @ConditionalOnMissingBean(name = "kafkaProducerFactory")
    public ProducerFactory<String, String> kafkaProducerFactory() {
        return new DefaultKafkaProducerFactory<>(
                kafkaProperties.buildProducerProperties());
    }

    @Bean
    @ConditionalOnMissingBean(name = "kafkaTransactionProducerFactory")
    public ProducerFactory<String, String> kafkaTransactionProducerFactory() {
        DefaultKafkaProducerFactory<String, String> factory = new DefaultKafkaProducerFactory<>(
                kafkaProperties.buildProducerProperties());
        String transactionIdPrefix = kafkaProperties.getProducer().getTransactionIdPrefix();
        if (transactionIdPrefix != null) {
            //添加事务id前缀
            factory.setTransactionIdPrefix(transactionIdPrefix);
        }
        return factory;
    }

    @Bean
    @Primary
    public ProducerListener<String, String> kafkaProducerListener() {
        return new LoggingProducerListener<>();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "kafkaTemplate")
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> kafkaProducerFactory,
                                                       ProducerListener<String, String> kafkaProducerListener,
                                                       ObjectProvider<RecordMessageConverter> messageConverter) {
        return getKafkaTemplate(kafkaProducerFactory, kafkaProducerListener, messageConverter);
    }

    @Bean
    @ConditionalOnMissingBean(name = "kafkaTransactionTemplate")
    public KafkaTemplate<String, String> kafkaTransactionTemplate(ProducerFactory<String, String> kafkaTransactionProducerFactory,
                                                                  ProducerListener<String, String> kafkaProducerListener,
                                                                  ObjectProvider<RecordMessageConverter> messageConverter) {
        return getKafkaTemplate(kafkaTransactionProducerFactory, kafkaProducerListener, messageConverter);
    }

    private KafkaTemplate<String, String> getKafkaTemplate(ProducerFactory<String, String> kafkaTransactionProducerFactory, ProducerListener<String, String> kafkaProducerListener, ObjectProvider<RecordMessageConverter> messageConverter) {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(kafkaTransactionProducerFactory);
        messageConverter.ifUnique(kafkaTemplate::setMessageConverter);
        kafkaTemplate.setProducerListener(kafkaProducerListener);
        if (StringUtils.isNotBlank(kafkaProperties.getTemplate().getDefaultTopic())) {
            kafkaTemplate.setDefaultTopic(kafkaProperties.getTemplate().getDefaultTopic());
        }
        return kafkaTemplate;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.kafka.producer.transaction-id-prefix")
    @ConditionalOnMissingBean
    public KafkaTransactionManager<?, ?> kafkaTransactionManager(ProducerFactory<String, String> kafkaTransactionProducerFactory) {
        return new KafkaTransactionManager<>(kafkaTransactionProducerFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaProducer kafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        StreamProducer.Config config = StreamProducer.Config.builder().build();
        KafkaProducerFactory factory = new KafkaProducerFactory(kafkaTemplate);
        return (KafkaProducer) factory.create(config);
    }

    /*consumer configuration.*/


    @Bean
    @ConditionalOnMissingBean(ConsumerFactory.class)
    public DefaultKafkaConsumerFactory<String, String> kafkaConsumerFactory (
            ObjectProvider<DefaultKafkaConsumerFactoryCustomizer> customizers) {
        DefaultKafkaConsumerFactory<String, String> factory = new DefaultKafkaConsumerFactory<>(
                this.kafkaProperties.buildConsumerProperties());
        customizers.orderedStream().forEach((customizer) -> customizer.customize(factory));
        return factory;
    }

    /**
     * 批量消费工厂
     */
    @Bean
    public KafkaListenerContainerFactory<?> batchFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties()));
        //设置为批量消费，每个批次数量在Kafka配置参数中设置ConsumerConfig.MAX_POLL_RECORDS_CONFIG
        factory.setBatchListener(true);
//        factory.setConcurrency(concurrency);
        //AckMode
        /* 当每一条记录被消费者监听器（ListenerConsumer）处理之后提交
                RECORD,
                 当每一批poll()的数据被消费者监听器（ListenerConsumer）处理之后提交
                BATCH,
                 当每一批poll()的数据被消费者监听器（ListenerConsumer）处理之后，距离上次提交时间大于TIME时提交
                TIME,
                 当每一批poll()的数据被消费者监听器（ListenerConsumer）处理之后，被处理record数量大于等于COUNT时提交
                COUNT,
                 TIME |　COUNT　有一个条件满足时提交
                COUNT_TIME,
                 当每一批poll()的数据被消费者监听器（ListenerConsumer）处理之后, 手动调用Acknowledgment.acknowledge()后提交
                MANUAL,
                 手动调用Acknowledgment.acknowledge()后立即提交
                MANUAL_IMMEDIATE,*/
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }


    /**
     * 单条数据消费工厂
     */
    @Bean
    public KafkaListenerContainerFactory<?> singleFactory(KafkaTemplate<String, String> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties()));
        //设置为批量消费，每个批次数量在Kafka配置参数中设置ConsumerConfig.MAX_POLL_RECORDS_CONFIG
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setReplyTemplate(kafkaTemplate);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }



    @Bean
    @ConditionalOnMissingBean
    public MqTransactionalService mqTransactionalService(MqMessageOperations operations, KafkaProducer kafkaProducer) {
        return new KafkaTransactionalService(operations, kafkaProducer);
    }



}
