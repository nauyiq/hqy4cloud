package com.hqy.mq.rabbitmq;

import com.hqy.util.spring.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/6 16:55
 */
public class RabbitmqProcessor {

    private static final Logger log = LoggerFactory.getLogger(RabbitmqProcessor.class);

    private RabbitmqProcessor() {}

    private static volatile RabbitmqProcessor instance = null;

    public static RabbitmqProcessor getInstance() {
        if (instance == null) {
            synchronized (RabbitmqProcessor.class) {
                if (instance == null) {
                    instance = new RabbitmqProcessor();
                }
            }
        }
        return instance;
    }


    public void sendMessage(String exchange, String routingKey, Object data) {
        sendMessage(exchange, routingKey, data, null);
    }

    public void sendMessage(String exchange, String routingKey, Object data, CorrelationData correlationData) {
        if (StringUtils.isAnyBlank(exchange, routingKey) || Objects.isNull(data)) {
            log.warn("@@@ Send message to Rabbitmq server failure, parameter undefined.");
            return;
        }

         /*
          单独为设置TTL的时候 当队列的消息没有被消费而过期时
          消息不会放到死信队列中 而单独为QUEUE设置TTL时 消息过期将会放到死信队列里面
          两者都设置TTL的时候 则以最小的值为准.
         */
        MessagePostProcessor messagePostProcessor = message -> {
            message.getMessageProperties().setContentEncoding("UTF-8");
            return message;
        };
        RabbitTemplate rabbitTemplate = SpringContextHolder.getBean(RabbitTemplate.class);

        if (Objects.isNull(correlationData)) {
            rabbitTemplate.convertAndSend(exchange, routingKey, data, messagePostProcessor);
        } else {
            rabbitTemplate.convertAndSend(exchange, routingKey, data, messagePostProcessor, correlationData);
        }
    }




}
