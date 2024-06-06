package com.hqy.cloud.foundation.redis.stream.support;

import com.hqy.cloud.foundation.redis.stream.RedisStreamMessageListener;
import com.hqy.cloud.foundation.redis.stream.RedisStreamService;
import com.hqy.cloud.stream.api.AbstractStreamConsumerFactory;
import com.hqy.cloud.stream.api.StreamConsumer;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/10
 */
public class RedisStreamConsumerFactory extends AbstractStreamConsumerFactory {
    private final RedisStreamService redisStreamService;
    private final RedisStreamMessageListener listener;

    public RedisStreamConsumerFactory(RedisStreamService redisStreamService, RedisStreamMessageListener listener) {
        this.redisStreamService = redisStreamService;
        this.listener = listener;
    }

    @Override
    protected StreamConsumer doCreate(StreamConsumer.Config config) {
        return new RedisStreamConsumer(redisStreamService, config, listener);
    }
}
