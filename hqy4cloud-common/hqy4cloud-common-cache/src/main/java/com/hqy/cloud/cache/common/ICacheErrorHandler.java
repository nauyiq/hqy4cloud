package com.hqy.cloud.cache.common;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 缓存异常处理.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/28
 */
@Slf4j
public class ICacheErrorHandler extends SimpleCacheErrorHandler {
    private ICacheErrorHandler() {
        super();
    }

    private static final ICacheErrorHandler INSTANCE = new ICacheErrorHandler();

    public static ICacheErrorHandler getInstance() {
        return INSTANCE;
    }


    /**
     * 计数器, 主要防止打印太多日志数据
     */
    private static final Map<CacheErrorType, AtomicLong> COUNT_LIMITER = MapUtil.newConcurrentHashMap(CacheErrorType.values().length);

    /**
     * 步长， 防止打印太多
     */
    private final static long STEP = 30;

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        printLog(CacheErrorType.CacheGet, cache, key, null, exception);
        super.handleCacheGetError(exception, cache, key);
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        printLog(CacheErrorType.CachePut, cache, key, value, exception);
        super.handleCachePutError(exception, cache, key, value);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        printLog(CacheErrorType.CacheEvict, cache, key, null, exception);
        super.handleCacheEvictError(exception, cache, key);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        printLog(CacheErrorType.CacheClear, cache, null, null, exception);
        super.handleCacheClearError(exception, cache);
    }

    /**
     * 打印日志.
     * @param type      cache异常类型
     * @param cache     cache上下文
     * @param key       缓存的key
     * @param value     缓存的value
     * @param exception 抛出的异常.
     */
    private void printLog(CacheErrorType type, Cache cache, Object key, Object value, RuntimeException exception) {
        // 获取计数器.
        AtomicLong count = COUNT_LIMITER.computeIfAbsent(type, v -> new AtomicLong(0));
        if (count.getAndIncrement() % STEP == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("[CacheError]").append(StrUtil.BRACKET_START).append(type).append(StrUtil.BRACKET_END)
                    .append("cacheName=").append(cache.getName());
            if (key != null) {
                sb.append(StrUtil.COMMA).append("key=").append(key);
            }
            if (value != null) {
                sb.append(StrUtil.COMMA).append("value=").append(value);
            }
            sb.append(StrUtil.CRLF).append("cause:").append(exception.getMessage());
            log.error(sb.toString());
        }
    }

    public enum CacheErrorType {

        /**
         * 异常类型.
         */
        CacheGet, CachePut, CacheEvict, CacheClear

    }


}
