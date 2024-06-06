package com.hqy.cloud.mq.rabbit.canal;

import com.hqy.cloud.canal.core.CanalGlue;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.core.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.hqy.cloud.mq.rabbit.lang.RabbitConstants.CANAL_RABBIT_QUEUE;

/**
 * 监听收到canal投递到rabbitmq的消息，在调用canal胶水层
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/27
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = CANAL_RABBIT_QUEUE)
public class RabbitCanalListener {
    private final CanalGlue canalGlue;

    @RabbitHandler
    public void action(@Payload String value, Message message, Channel channel) throws IOException {
        if (StringUtils.isBlank(value)) {
            log.warn("Receive canal empty message by rabbit.");
            return;
        }
        try {
            canalGlue.process(value);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            // nack并且消息不返回队列.
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }


}
