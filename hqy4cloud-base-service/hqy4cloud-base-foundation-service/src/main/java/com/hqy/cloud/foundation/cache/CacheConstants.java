package com.hqy.cloud.foundation.cache;

import com.hqy.cloud.common.base.lang.NumberConstants;

import java.time.Duration;

/**
 * 缓存常量类.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/27 15:27
 */
public interface CacheConstants {

    /**
     * 缓存默认过期（有效）时间
     * 默认一小时
     */
    Duration CACHE_DEFAULT_EXPIRE = Duration.ofMillis(NumberConstants.ONE_HOUR_4MILLISECONDS);

    String DEFAULT_KEY_GENERATOR_NAME = "iKeyGenerator";

    /**
     * spring boot cache cache manager names
     */
    interface Manager {

        /**
         * redis cache
         */
        String REDIS_CACHE_MANAGE = "redisCacheManager";

        /**
         * caffeine cache
         */
        String CAFFEINE_CACHE_MANAGER = "caffeineCacheManager";
    }



}
