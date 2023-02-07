package com.hqy.mq.rabbitmq.server;

import com.hqy.mq.common.MessageModel;
import com.hqy.mq.common.server.Consumer;
import com.hqy.mq.common.server.support.AbstractMqListener;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.messaging.handler.annotation.Payload;

import java.io.IOException;
import java.util.Objects;

/**
 * rabbitmq监听器
 * @see com.hqy.mq.common.server.support.AbstractMqListener
 * RabbitMq 消费消息有三种回执方法 <br>
 * 1. basicAck:表示成功确认，使用此回执方法后，消息会被rabbitmq broker 删除。 deliveryTag：表示消息投递序号  multiple：是否批量确认，值为true则会一次性ack所有小于当前消息deliveryTag的消息。
 * 2. basicNack:表示失败确认，一般在消费消息业务异常时用到此方法，可以将消息重新投递入队列。 deliveryTag：表示消息投递序号。multiple：是否批量确认。requeue：值为 true 消息将重新入队列。
 * 3. basicReject:拒绝消息，与basicNack区别在于不能进行批量操作，其他用法很相似。 deliveryTag：表示消息投递序号。requeue：值为 true 消息将重新入队列。
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/7 14:29
 */
@Slf4j
public abstract class RabbitmqListener<T extends MessageModel> extends AbstractMqListener<T> {
    public RabbitmqListener(Consumer<T> consumer) {
        super(consumer);
    }

    @RabbitHandler
    public void process(@Payload T payload, Message message, Channel channel) throws IOException {
        if (Objects.isNull(payload)) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
        try {
            this.notify(payload);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Throwable cause) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

}
