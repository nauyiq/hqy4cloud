package com.hqy.cloud.mq.rocket.lang;

import com.alibaba.fastjson2.JSON;
import com.hqy.cloud.mq.rocket.server.StreamBridgeProducer;
import com.hqy.cloud.stream.core.MessageBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;

/**
 * @author hongqy
 * @date 2025/6/26
 */
@Slf4j
public class StreamMessageUtil {

    public static <T> T getMessage(Message<MessageBody> msg, Class<T> type) {
        String messageId = msg.getHeaders().get(StreamBridgeProducer.ROCKET_MQ_MESSAGE_ID, String.class);
        String tag = msg.getHeaders().get(StreamBridgeProducer.ROCKET_TAGS, String.class);
        String topic = msg.getHeaders().get(StreamBridgeProducer.ROCKET_MQ_TOPIC, String.class);
        String body = msg.getPayload().getBody();
        log.info("Received Message topic:{} messageId:{}, body:{}， tag:{}", topic, messageId, body, tag);
        return JSON.parseObject(body, type);
    }
}
