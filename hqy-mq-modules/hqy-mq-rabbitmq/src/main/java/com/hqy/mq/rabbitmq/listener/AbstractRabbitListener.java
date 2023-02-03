package com.hqy.mq.rabbitmq.listener;

import com.hqy.base.common.base.lang.exception.MessageMqException;
import com.hqy.mq.common.listener.payload.MessagePayload;
import com.hqy.mq.rabbitmq.config.RabbitmqAutoConfiguration;
import com.hqy.mq.rabbitmq.listener.strategy.ListenerStrategy;
import com.hqy.util.AssertUtil;
import com.hqy.util.JsonUtil;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RabbitMq 消费消息有三种回执方法 <br>
 * 1. basicAck:表示成功确认，使用此回执方法后，消息会被rabbitmq broker 删除。 deliveryTag：表示消息投递序号  multiple：是否批量确认，值为true则会一次性ack所有小于当前消息deliveryTag的消息。
 * 2. basicNack:表示失败确认，一般在消费消息业务异常时用到此方法，可以将消息重新投递入队列。 deliveryTag：表示消息投递序号。multiple：是否批量确认。requeue：值为 true 消息将重新入队列。
 * 3. basicReject:拒绝消息，与basicNack区别在于不能进行批量操作，其他用法很相似。 deliveryTag：表示消息投递序号。requeue：值为 true 消息将重新入队列。
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/6 17:15
 */
@Component
public abstract class AbstractRabbitListener<T extends MessagePayload> {

    private static final Logger log = LoggerFactory.getLogger(AbstractRabbitListener.class);

    private static final Map<String, Integer> RETRY_MAP = new ConcurrentHashMap<>();

    /**
     * listener message action strategy.
     * @return ListenerStrategy
     */
    public abstract ListenerStrategy<T> strategy();


    @RabbitHandler
    public void process(@Payload T payload, Channel channel, Message message) throws IOException {
        try {
            ListenerStrategy<T> strategy = strategy();
            AssertUtil.notNull(strategy, "Not found strategy, payload: = " + JsonUtil.toJson(payload));
            //do action.
            strategy.action(payload);
            //No throw exception, return ack ok.
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (MessageMqException mqException) {
            String messageId = payload.obtainMessageId();
            log.warn("@@@ Consumer message throwException, messageId:{}", messageId);
            int retryTime = 1;
            if (RETRY_MAP.containsKey(messageId)) {
                retryTime = RETRY_MAP.get(messageId);
            }
            boolean retry = retryTime < RabbitmqAutoConfiguration.RETRY_TIME;
            try {
                if (!retry) {
                    RETRY_MAP.remove(messageId);
                    strategy().compensate(payload);
                } else {
                    RETRY_MAP.put(messageId, ++retryTime);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, retry);
                log.info("[AbstractRabbitListener] action message throw mqException, {}, {}, {}", messageId, retryTime, retry);
            }
        }
        catch (Throwable e) {
            log.error(e.getMessage(), e);
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                strategy().compensate(payload);
            } catch (Exception exception) {
                log.error(e.getMessage(), e);
            }
        }


    }

}
