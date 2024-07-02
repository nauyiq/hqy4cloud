package com.hqy.cloud.cache.common;

import com.hqy.cloud.common.base.lang.NumberConstants;
import com.hqy.cloud.common.base.lang.StringConstants;

import java.time.Duration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
public class RedisConstants {

    /**
     * spring cache 键生成器bean名字
     */
    public static final String DEFAULT_KEY_GENERATOR_NAME = "iKeyGenerator";

    /**
     * redis cache manager
     */
    public static final String REDIS_CACHE_MANAGE = "redisCacheManager";

    /**
     * caffeine cache manager
     */
    public static final String CAFFEINE_CACHE_MANAGER = "caffeineCacheManager";

    /**
     * 默认的redis template bean名字
     */
    public static final String DEFAULT_REDIS_TEMPLATE_BEAN_NAME = "ManagerRedisTemplate";

    /**
     * 缓存默认过期（有效）时间
     * 默认一小时
     */
    public static Duration CACHE_DEFAULT_EXPIRE = Duration.ofMillis(NumberConstants.ONE_HOUR_4MILLISECONDS);

    /**
     * 缓存key分隔符
     */
    public static final String CACHE_KEY_SEPARATOR = StringConstants.Symbol.COLON;


    /**
     * redis
     */
    public static final String REDIS = "redis";

    /**
     * 默认可查看1000条pending的消息
     */
    public static final int STREAM_DEFAULT_PENDING_LIMIT_COUNT = 1000;

    public static final String REDIS_STREAM_CONSUMER_ENABLED = "spring.redis.stream.consumer.enabled";

    public static final String REDIS_STREAM_CONSUMER_GROUP_ID = "spring.redis.stream.consumer.group-id";

    public static final String REDIS_STREAM_CONSUMER_GROUP_MAX_BATCH_SIZE = "spring.redis.stream.consumer.batch-size";



}
