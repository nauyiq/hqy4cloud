package com.hqy.cloud.cache.common;

import org.apache.commons.lang3.StringUtils;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
public class RedisKeyUtil {

    public static String getCacheKey(String prefix, String cacheName) {
        return getCacheKey(null, prefix, cacheName);
    }

    public static String getCacheKey(String project, String prefix, String cacheName) {
        return getCacheKey(null, project, prefix, cacheName);
    }

    public static String getCacheKey(String env, String project, String prefix, String cacheName){
        if (StringUtils.isNotBlank(cacheName)) {
            throw new UnsupportedOperationException("Redis cache name should not be empty.");
        }
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(env)) {
            sb.append(env).append(RedisConstants.CACHE_KEY_SEPARATOR);
        }
        if (StringUtils.isNotBlank(project)) {
            sb.append(project).append(RedisConstants.CACHE_KEY_SEPARATOR);
        }
        if (StringUtils.isNotBlank(prefix)) {
            sb.append(prefix).append(RedisConstants.CACHE_KEY_SEPARATOR);
        }
        sb.append(cacheName);
        return sb.toString();
    }


}
