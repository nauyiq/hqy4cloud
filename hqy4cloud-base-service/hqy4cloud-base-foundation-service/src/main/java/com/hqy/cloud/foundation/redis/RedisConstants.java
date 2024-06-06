package com.hqy.cloud.foundation.redis;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/23
 */
public interface RedisConstants {

    /**
     * 默认可查看1000条pending的消息
     */
    int STREAM_DEFAULT_PENDING_LIMIT_COUNT = 1000;

    /**
     * redis
     */
    String REDIS = "redis";

    String REDIS_STREAM_CONSUMER_ENABLED = "spring.redis.stream.consumer.enabled";

    String REDIS_STREAM_CONSUMER_GROUP_ID = "spring.redis.stream.consumer.group-id";

    String REDIS_STREAM_CONSUMER_GROUP_MAX_BATCH_SIZE = "spring.redis.stream.consumer.batch-size";






}
