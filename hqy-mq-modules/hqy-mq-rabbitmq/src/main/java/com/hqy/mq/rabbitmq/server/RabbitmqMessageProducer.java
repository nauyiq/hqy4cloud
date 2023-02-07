package com.hqy.mq.rabbitmq.server;

import cn.hutool.core.convert.Convert;
import com.hqy.base.common.base.lang.exception.MessageQueueException;
import com.hqy.base.common.support.Parameters;
import com.hqy.mq.common.MessageModel;
import com.hqy.mq.common.lang.enums.MessageQueue;
import com.hqy.mq.common.server.support.AbstractProducer;
import com.hqy.mq.rabbitmq.lang.RabbitmqMessageModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.hqy.base.common.base.lang.exception.MessageQueueException.FAILED_SEND_MESSAGE;
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

    /**
     * 构建rabbitmq消息对象
     * @param message message model.
     * @return        RabbitmqMessageModel.
     */
    protected abstract <T extends MessageModel> RabbitmqMessageModel buildMessage(T message);

    protected void doFailure(RabbitmqMessageModel message, Throwable ex) {
        log.error("Failed execute to send message to rabbitmq, message: {}.", message.payload(), ex);
    }

    protected void doSuccess(RabbitmqMessageModel model, CorrelationData.Confirm result) {
        if (result.isAck()) {
           if (log.isDebugEnabled()) {
               log.debug("Send message to rabbitmq success, message: {}.", model.payload());
           }
        } else {
            doFailure(model, new MessageQueueException(FAILED_SEND_MESSAGE, "Send rabbitmq message return no ack."));
        }
    }

    @Override
    protected <T extends MessageModel> void sendMessage(T message) throws MessageQueueException {
        RabbitmqMessageModel model = buildMessage(message);
        if (Objects.isNull(model) ||
                StringUtils.isAnyBlank(model.exchange(), model.routingKey(), model.payload())) {
            throw new MessageQueueException(MessageQueueException.EMPTY_MESSAGE_CODE, "Rabbitmq message should not be null.");
        }

        MessagePostProcessor processor = getMessagePostProcessor(model);
        CorrelationData correlationData = new CorrelationData();
        correlationData.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
            @Override
            public void onFailure(@Nullable Throwable ex) {
                doFailure(model, ex);
            }
            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                doSuccess(model, result);
            }
        });

        rabbitTemplate.convertAndSend(model.exchange(), model.routingKey(), model.payload(), processor, correlationData);
    }

    private MessagePostProcessor getMessagePostProcessor(RabbitmqMessageModel model) {
        return rabbitMessage -> {
            //设置编码
            rabbitMessage.getMessageProperties().setContentEncoding(StandardCharsets.UTF_8.name());
            //设置投递模式 默认为持久化模式
            rabbitMessage.getMessageProperties().setDeliveryMode(MessageProperties.DEFAULT_DELIVERY_MODE);
            //设置消息id
            rabbitMessage.getMessageProperties().setMessageId(model.messageId());
            Parameters parameters = model.parameters();
            if (Objects.nonNull(parameters)) {
                //设置消息优先级
                String priority = parameters.getParameter(QUEUE_MAX_PRIORITY_KEY);
                if (StringUtils.isNotBlank(priority)) {
                    rabbitMessage.getMessageProperties().setPriority(Convert.toInt(priority));
                }
                //设置消息ttl，如果队列已经设置了ttl 则以最小值为准
                String ttl = parameters.getParameter(TTL);
                if (StringUtils.isNotBlank(ttl)) {
                    rabbitMessage.getMessageProperties().setExpiration(ttl);
                }
            }
            return rabbitMessage;
        };
    }


}
