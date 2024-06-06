package com.hqy.cloud.mq.rabbit.server;

import cn.hutool.core.convert.Convert;
import com.hqy.cloud.common.base.lang.exception.MessageQueueException;
import com.hqy.cloud.mq.rabbit.lang.RabbitMessage;
import com.hqy.cloud.stream.api.AbstractStreamProducerTemplate;
import com.hqy.cloud.stream.api.MessageId;
import com.hqy.cloud.stream.api.StreamCallback;
import com.hqy.cloud.stream.api.StreamMessage;
import com.hqy.cloud.stream.core.CompletableFutureResult;
import com.hqy.cloud.mq.rabbit.lang.RabbitConstants;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/22
 */
public class RabbitmqProducer extends AbstractStreamProducerTemplate<Void> {
    private final RabbitTemplate rabbitTemplate;
    public RabbitmqProducer(Config config, RabbitTemplate rabbitTemplate) {
        super(config);
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    protected <K extends Comparable<K>, V> Void doSyncSendMessage(StreamMessage<K, V> message) {
        if (message instanceof RabbitMessage rabbitMessage) {
            MessagePostProcessor processor = getProcessor(rabbitMessage);
            rabbitTemplate.convertAndSend(rabbitMessage.getExchange(), rabbitMessage.getRoutingKey(), rabbitMessage, processor, rabbitMessage.getCorrelationData());
            return null;
        }
        throw new MessageQueueException("Unknown stream message type, just support rabbit message.");
    }

    @Override
    protected <K extends Comparable<K>, V> void doAsyncSendMessage(StreamMessage<K, V> message, StreamCallback<Void> callback) {
        if (message instanceof RabbitMessage rabbitMessage) {
            MessagePostProcessor processor = getProcessor(rabbitMessage);
            CorrelationData correlationData = rabbitMessage.getCorrelationData();
            if (callback != null) {
                correlationData.getFuture().addCallback(new ListenableFutureCallback<>() {
                    @Override
                    public void onFailure(@Nonnull Throwable ex) {
                        callback.onFailed(ex);
                    }
                    @Override
                    public void onSuccess(CorrelationData.Confirm result) {
                        callback.onSuccess(null);
                    }
                });
            }
            rabbitTemplate.convertAndSend(rabbitMessage.getExchange(), rabbitMessage.getRoutingKey(), rabbitMessage, processor, correlationData);
        }
        throw new MessageQueueException("Unknown stream message type, just support rabbit message.");
    }

    @Override
    public String getType() {
        return RabbitConstants.RABBIT;
    }

    private MessagePostProcessor getProcessor(RabbitMessage rabbitMessage) {
        return m -> {
            m.getMessageProperties().setDeliveryMode(MessageProperties.DEFAULT_DELIVERY_MODE);
            m.getMessageProperties().setContentEncoding(StandardCharsets.UTF_8.name());
            // 设置消息ID
            MessageId<String> id = rabbitMessage.getId();
            if (id != null) {
                m.getMessageProperties().setMessageId(id.get());
            }
            // 设置消息TTL
            if (rabbitMessage.getAttributes().containsKey(RabbitConstants.TTL)) {
                m.getMessageProperties().setExpiration(rabbitMessage.getAttributes().get(RabbitConstants.TTL).toString());
            }
            // 设置队列优先级
            if (rabbitMessage.getAttributes().containsKey(RabbitConstants.QUEUE_MAX_PRIORITY_KEY)) {
                m.getMessageProperties().setPriority(Convert.toInt(rabbitMessage.getAttributes().get(RabbitConstants.QUEUE_MAX_PRIORITY_KEY)));
            }
            return m;
        };
    }
}
