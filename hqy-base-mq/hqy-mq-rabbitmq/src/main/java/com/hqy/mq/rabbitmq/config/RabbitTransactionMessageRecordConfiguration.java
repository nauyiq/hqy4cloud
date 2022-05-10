package com.hqy.mq.rabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 基于rabbitmq + 本地消息表 实现分布式事务.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/7 11:26
 */
@Slf4j
@Configuration
@SuppressWarnings("rawtypes")
@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "transactional", havingValue = "true" )
public class RabbitTransactionMessageRecordConfiguration {

    public static final String EXCHANGE = "global-transaction-exchange";

    public static final String QUEUE = "global-transaction-queue";


    @Bean
    FanoutExchange transactionalExchange() {
        return new FanoutExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue transactionalQueue() {
        return new Queue(QUEUE, true, false, false);
    }

    @Bean
    Binding transactionalBinding(FanoutExchange transactionalExchange, Queue transactionalQueue) {
        return BindingBuilder.bind(transactionalQueue).to(transactionalExchange);
    }



}
