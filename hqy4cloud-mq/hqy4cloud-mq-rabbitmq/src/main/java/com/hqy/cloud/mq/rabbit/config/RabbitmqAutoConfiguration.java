package com.hqy.cloud.mq.rabbit.config;

import com.hqy.cloud.canal.core.CanalGlue;
import com.hqy.cloud.mq.api.transactional.service.MqMessageOperations;
import com.hqy.cloud.mq.api.transactional.service.MqTransactionalService;
import com.hqy.cloud.mq.rabbit.canal.RabbitCanalListener;
import com.hqy.cloud.mq.rabbit.dynamic.RabbitModuleInitializer;
import com.hqy.cloud.mq.rabbit.dynamic.RabbitModuleProperties;
import com.hqy.cloud.mq.rabbit.server.RabbitTransactionalService;
import com.hqy.cloud.mq.rabbit.server.RabbitmqProducer;
import com.hqy.cloud.stream.api.StreamProducer;
import com.hqy.cloud.mq.rabbit.server.RabbitProducerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq auto configuration for nacos
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/6 16:00
 */
@Configuration
public class RabbitmqAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RabbitmqAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ConnectionFactory.class)
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);
        //消息确认机制 只要消息被成功投递到exchange 就会执行次回调 需要注意的是消息确认机制会降低rabbitmq的吞吐量
        rabbitTemplate.setConfirmCallback((correlationData, b, s) -> {
            log.info("ConfirmCallback: correlationData = {}", correlationData);
            log.info("ConfirmCallback: ack = {}", b);
            log.info("ConfirmCallback: cause = {}", s);
        });
        rabbitTemplate.setReturnsCallback(returned -> {
            log.info("ReturnCallback: message = {}", returned.getMessage());
            log.info("ReturnCallback: replyCode = {}", returned.getReplyCode());
            log.info("ReturnCallback: replyText = {}", returned.getReplyText());
            log.info("ReturnCallback: exchange = {}", returned.getExchange());
            log.info("ReturnCallback: routingKey = {}", returned.getRoutingKey());
        });
        return rabbitTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitmqProducer rabbitmqProducer(RabbitTemplate rabbitTemplate) {
        RabbitProducerFactory factory = new RabbitProducerFactory(rabbitTemplate);
        StreamProducer.Config config = StreamProducer.Config.builder().build();
        return (RabbitmqProducer) factory.create(config);
    }

    @Bean
    @ConditionalOnBean
    @ConditionalOnMissingBean
    public RabbitCanalListener rabbitCanalListener(CanalGlue canalGlue) {
        return new RabbitCanalListener(canalGlue);
    }

    /**
     * 消息序列化配置
     */
    @Bean
    @ConditionalOnBean
    @ConditionalOnMissingBean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

    /**
     * 动态创建队列、交换机初始化器
     */
    @Bean
    @ConditionalOnBean
    @ConditionalOnMissingBean
    public RabbitModuleInitializer rabbitModuleInitializer(AmqpAdmin amqpAdmin, RabbitModuleProperties rabbitModuleProperties) {
        return new RabbitModuleInitializer(amqpAdmin, rabbitModuleProperties);
    }

    @Bean
    @ConditionalOnBean
    @ConditionalOnMissingBean
    public MqTransactionalService mqTransactionalService(MqMessageOperations operations, RabbitmqProducer producer) {
        return new RabbitTransactionalService(operations, producer);
    }


}
