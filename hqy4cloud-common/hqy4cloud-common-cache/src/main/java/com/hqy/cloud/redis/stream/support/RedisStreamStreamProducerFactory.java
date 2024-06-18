package com.hqy.cloud.redis.stream.support;

import com.hqy.cloud.redis.stream.RedisStreamService;
import com.hqy.cloud.stream.api.AbstractStreamProducerFactory;
import com.hqy.cloud.stream.api.StreamProducer;
import lombok.RequiredArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/6
 */
@RequiredArgsConstructor
public class RedisStreamStreamProducerFactory extends AbstractStreamProducerFactory<String> {
    private final RedisStreamService redisStreamService;

    @Override
    protected StreamProducer<String> doCreate(StreamProducer.Config config) {
        return new RedisStreamMessageProducer(config, redisStreamService);
    }
}
