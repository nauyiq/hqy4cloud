package com.hqy.mq.rabbitmq.server;

import cn.hutool.core.convert.Convert;
import com.hqy.cloud.common.base.lang.exception.MessageQueueException;
import com.hqy.mq.common.bind.MessageModel;
import com.hqy.mq.common.bind.MessageParams;
import com.hqy.mq.common.lang.enums.MessageQueue;
import com.hqy.mq.common.server.support.AbstractProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;

import static com.hqy.mq.rabbitmq.lang.RabbitConstants.QUEUE_MAX_PRIORITY_KEY;
import static com.hqy.mq.rabbitmq.lang.RabbitConstants.TTL;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/7 14:48
 */
@Slf4j
public abstract class RabbitmqMessageProducer extends AbstractProducer {
    private final RabbitTemplate rabbitTemplate;

    public RabbitmqMessageProducer(RabbitTemplate rabbitTemplate) {
        super(MessageQueue.RABBITMQ);
        this.rabbitTemplate = rabbitTemplate;
    }

    protected <T extends MessageModel> void doFailure(T message, Throwable ex) {
        log.error("Failed execute to send message to rabbitmq, message: {} -> {}.", message.messageId(), message.jsonPayload(), ex);
    }

    protected <T extends MessageModel> void doSuccess(T message, CorrelationData.Confirm result) {
        if (result.isAck()) {
           if (log.isDebugEnabled()) {
               log.debug("Send message to rabbitmq success, message: {} -> {}.", message.messageId(), message.jsonPayload());
           }
        } else {
            doFailure(message, new MessageQueueException(MessageQueueException.FAILED_SEND_MESSAGE, "Send rabbitmq message return no ack."));
        }
    }

    @Override
    protected <T extends MessageModel> void sendMessage(T message) throws MessageQueueException {
        //交换机
        String exchange = message.getParameters().getTarget();
        //路由键
        String routingKey = message.getParameters().getKey();
        //消息json
        String jsonPayload = message.jsonPayload();
        if (StringUtils.isAnyBlank(exchange, routingKey, jsonPayload)) {
            throw new MessageQueueException(MessageQueueException.EMPTY_MESSAGE_PARAMS, "Rabbitmq message parameters should not be empty.");
        }

        MessagePostProcessor processor = getMessagePostProcessor(message);
        CorrelationData correlationData = new CorrelationData();
        correlationData.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
            @Override
            public void onFailure(@Nullable Throwable ex) {
                doFailure(message, ex);
            }
            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                doSuccess(message, result);
            }
        });

        rabbitTemplate.convertAndSend(exchange, routingKey, jsonPayload, processor, correlationData);
    }

    private <T extends MessageModel> MessagePostProcessor getMessagePostProcessor(T message) {
        return rabbitMessage -> {
            //设置编码
            rabbitMessage.getMessageProperties().setContentEncoding(StandardCharsets.UTF_8.name());
            //设置投递模式 默认为持久化模式
            rabbitMessage.getMessageProperties().setDeliveryMode(MessageProperties.DEFAULT_DELIVERY_MODE);
            //设置消息id
            rabbitMessage.getMessageProperties().setMessageId(message.messageId());
            MessageParams messageParameters = message.getParameters();
            //设置消息优先级
            String priority = messageParameters.getParameter(QUEUE_MAX_PRIORITY_KEY);
            if (StringUtils.isNotBlank(priority)) {
                rabbitMessage.getMessageProperties().setPriority(Convert.toInt(priority));
            }
            //设置消息ttl，如果队列已经设置了ttl 则以最小值为准
            String ttl = messageParameters.getParameter(TTL);
            if (StringUtils.isNotBlank(ttl)) {
                rabbitMessage.getMessageProperties().setExpiration(ttl);
            }
            return rabbitMessage;
        };
    }

    public RabbitTemplate getRabbitTemplate() {
        return rabbitTemplate;
    }
}
