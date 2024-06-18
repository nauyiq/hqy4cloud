package com.hqy.cloud.cache.redis.stream;

import com.hqy.cloud.stream.api.ErrorHandler;
import com.hqy.cloud.stream.api.StreamConsumer;
import com.hqy.cloud.stream.api.StreamMessageListener;
import com.hqy.cloud.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.stream.StreamListener;

import java.util.List;
import java.util.Map;

/**
 * redis stream消息监听器模板， 客户端消费stream通道消息时应继承该父类.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/10
 */
@Slf4j
public abstract class RedisStreamMessageListener implements StreamMessageListener, StreamListener<String, MapRecord<String, String, Object>>  {
    private final StreamConsumer.Config config;
    private final RedisStreamService redisStreamService;

    protected RedisStreamMessageListener(StreamConsumer.Config config, RedisStreamService redisStreamService) {
        this.config = config;
        this.redisStreamService = redisStreamService;
    }

    @Override
    public void onMessage(MapRecord<String, String, Object> message) {
        RecordId id = message.getId();
        String idValue = id.getValue();
        if (log.isDebugEnabled()) {
            log.debug("Do stream message, stream:{}, messageId: {}.", message.getStream(), idValue);
        }
        boolean result = true;
        Map<String, Object> value = message.getValue();
        Object bean = null;
        try {
            bean = JsonUtil.toBean(value, getMessageType());
            if (bean == null) {
                return;
            }
            this.onMessage(List.of(bean));
        } catch (Exception cause) {
            log.error("Failed execute to onMessage, cause:{}.", cause.getMessage());
            result = false;
            ErrorHandler errorHandler = config.getErrorHandler();
            if (errorHandler != null) {
                Object finalBean = bean;
                config.getExecutorService().execute(() -> errorHandler.onError(finalBean, cause));
            }
        } finally {
            if (config.isAutoAck() && result) {
                // ack消息
                redisStreamService.ack(message.getStream(), config.getGroup(), idValue);
                // 删除消息
                redisStreamService.del(message.getStream(), List.of(idValue));
            }
        }
    }
}
