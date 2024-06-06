package com.hqy.cloud.foundation.redis.stream.support;

import com.hqy.cloud.foundation.redis.RedisConstants;
import com.hqy.cloud.foundation.redis.stream.RedisStreamService;
import com.hqy.cloud.stream.api.AbstractStreamProducerTemplate;
import com.hqy.cloud.stream.api.MessageId;
import com.hqy.cloud.stream.api.StreamCallback;
import com.hqy.cloud.stream.api.StreamMessage;
import com.hqy.cloud.stream.core.CompletableFutureResult;
import lombok.extern.slf4j.Slf4j;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/25
 */
@Slf4j
public class RedisStreamMessageProducer extends AbstractStreamProducerTemplate<String> {
    private final RedisStreamService redisStreamService;

    public RedisStreamMessageProducer(Config config, RedisStreamService redisStreamService) {
        super(config);
        this.redisStreamService = redisStreamService;
    }

    @Override
    protected <K extends Comparable<K>, V> String doSyncSendMessage(StreamMessage<K, V> message) {
        MessageId<K> messageId = message.getId();
        String topic = message.getTopic();
        String stream = getStream(topic);
        return messageId != null ? redisStreamService.add(stream, messageId.get().toString(), message) :
                        redisStreamService.add(stream, message);
    }

    @Override
    protected <K extends Comparable<K>, V> void doAsyncSendMessage(StreamMessage<K, V> message, StreamCallback<String> callback) {
        // 不支持异步发消息.
        log.warn("The {} stream producer not support async send message api.", getType());
        throw new UnsupportedOperationException(getType() + " not support async message api.");
    }


    @Override
    public String getType() {
        return RedisConstants.REDIS;
    }

    private String getStream(String messageTopic) {
        return "Stream::" + messageTopic;
     }

    public RedisStreamService getRedisStreamService() {
        return redisStreamService;
    }
}
