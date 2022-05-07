package com.hqy.mq.rabbitmq.config;

import com.hqy.mq.common.service.DeliveryMessageService;
import com.hqy.mq.rabbitmq.RabbitmqProcessor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 基于rabbitmq + 本地消息表 实现分布式事务.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/7 11:26
 */
@Configuration
@SuppressWarnings("rawtypes")
@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "transactional", value = "true")
public class RabbitTransactionMessageRecordConfiguration {

    public static final String EXCHANGE = "global-transaction-exchange";

    public static final String QUEUE = "global-transaction-queue";

    public static final String ROOTING_KEY = "global-transaction-key";

    @Bean
    DirectExchange transactionalDirectExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue transactionalQueue() {
        return new Queue(QUEUE, true, false, false);
    }

    @Bean
    Binding transactionalBinding(DirectExchange transactionalDirectExchange, Queue transactionalQueue) {
        return BindingBuilder.bind(transactionalQueue).to(transactionalDirectExchange).with(ROOTING_KEY);
    }

    @Bean
    public DeliveryMessageService rabbitDeliveryService() {
        return messageRecord -> {
            try {
                RabbitmqProcessor.getInstance().sendMessage(EXCHANGE, ROOTING_KEY, messageRecord,
                        new CorrelationData(messageRecord.getMessageId()));
            } catch (Exception e) {
                return false;
            }
            return true;
        };
    }

}
