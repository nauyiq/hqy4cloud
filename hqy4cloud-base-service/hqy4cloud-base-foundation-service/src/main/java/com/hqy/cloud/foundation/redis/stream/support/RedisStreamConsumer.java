package com.hqy.cloud.foundation.redis.stream.support;

import com.hqy.cloud.foundation.redis.RedisConstants;
import com.hqy.cloud.foundation.redis.exception.RedisException;
import com.hqy.cloud.foundation.redis.stream.RedisStreamMessageListener;
import com.hqy.cloud.foundation.redis.stream.RedisStreamService;
import com.hqy.cloud.stream.api.AbstractStreamConsumerTemplate;
import com.hqy.cloud.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.util.Objects;

/**
 * redis stream 消费者.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/7
 */
@Slf4j
public class RedisStreamConsumer extends AbstractStreamConsumerTemplate {
    private final RedisStreamService redisStreamService;
    private StreamMessageListenerContainer<String, MapRecord<String, String, Object>> container;
    private Subscription subscription;

    public RedisStreamConsumer(RedisStreamService redisStreamService, Config config, RedisStreamMessageListener listener) {
        super(config, listener);
        this.redisStreamService = redisStreamService;
    }

    public RedisStreamService getRedisStreamService() {
        return redisStreamService;
    }


    @Override
    protected void onInit() {
        Config config = getConfig();
        String stream = getStream(config.getTopic());

        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        serializer.setObjectMapper(JsonUtil.MAPPER);

        // 创建消费者消息容器
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, Object>> options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .batchSize((int) config.getBatchSize())
                .executor(config.getExecutorService().getExecutorService())
                .pollTimeout(config.getTimeout())
                .hashValueSerializer(serializer)
                .hashKeySerializer(new StringRedisSerializer())
                .build();
        this.container = StreamMessageListenerContainer.create(Objects.requireNonNull(redisStreamService.getTemplate().getConnectionFactory()), options);

        Consumer consumer = initStreamConsumerGroupAndReturnConsumer(stream, config);
        // 创建订阅对象.
        subscription = config.isAutoAck() ? container.receiveAutoAck(consumer, StreamOffset.create(stream, ReadOffset.lastConsumed()), (RedisStreamMessageListener) getListener()) :
                container.receive(consumer, StreamOffset.create(stream, ReadOffset.lastConsumed()), (RedisStreamMessageListener) getListener());

    }

    private Consumer initStreamConsumerGroupAndReturnConsumer(String redisStream, Config config) {
        StreamInfo.XInfoGroups groups = redisStreamService.getTemplate().opsForStream().groups(redisStream);
        boolean match = groups.stream().anyMatch(group -> group.groupName().equals(config.getGroup()));
        Consumer consumer = Consumer.from(config.getGroup(), config.getName());
        if (!match) {
            // 需要创建消费者组.
            try {
                redisStreamService.createConsumerGroup(redisStream, config.getGroup());
            } catch (Throwable cause) {
                log.error("Failed execute to create consumer group by stream: {}, group: {}.", redisStream, config.getGroup());
                throw new RedisException(cause.getMessage(), cause);
            }
        }
        return consumer;
    }

    @Override
    protected void onStart() {
        // 启动容器
        if (this.container != null) {
            this.container.start();
        }
    }

    @Override
    protected void onClose() {
        // 关闭容器
        if (this.container != null) {
            this.container.stop();
        }
    }

    public StreamMessageListenerContainer<String, MapRecord<String, String, Object>> getContainer() {
        return container;
    }

    public Subscription getSubscription() {
        return subscription;
    }


    private String getStream(String messageTopic) {
        return "Stream::" + messageTopic;
    }


    @Override
    public String getType() {
        return RedisConstants.REDIS;
    }
}
