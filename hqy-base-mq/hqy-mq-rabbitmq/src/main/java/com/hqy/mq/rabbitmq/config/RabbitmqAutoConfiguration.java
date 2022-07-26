package com.hqy.mq.rabbitmq.config;

import com.hqy.mq.rabbitmq.dynamic.RabbitModuleInitializer;
import com.hqy.mq.rabbitmq.dynamic.RabbitModuleProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * rabbitmq auto configuration for nacos
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/6 16:00
 */
@Configuration
public class RabbitmqAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RabbitmqAutoConfiguration.class);

    @Value("${spring.rabbitmq.retry:3}")
    public static int RETRY_TIME = 3;

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

        //消息退回机制 只要消息投递到exchange失败 就会执行次回调
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("ReturnCallback: message = {}", message);
            log.info("ReturnCallback: replyCode = {}", replyCode);
            log.info("ReturnCallback: replyText = {}", replyText);
            log.info("ReturnCallback: exchange = {}", exchange);
            log.info("ReturnCallback: routingKey = {}", routingKey);
        });

        return rabbitTemplate;
    }


    /**
     * 消息序列化配置
     */
    @Bean
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
    @ConditionalOnMissingBean
    public RabbitModuleInitializer rabbitModuleInitializer(AmqpAdmin amqpAdmin, RabbitModuleProperties rabbitModuleProperties) {
        return new RabbitModuleInitializer(amqpAdmin, rabbitModuleProperties);
    }


}
