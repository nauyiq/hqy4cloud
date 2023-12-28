package com.hqy.cloud.foundation.cache.config;

import com.hqy.cloud.foundation.cache.ICacheErrorHandler;
import com.hqy.cloud.foundation.cache.IKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/27 16:51
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnBean(RedisCacheManager.class)
public class CacheConfiguration extends CachingConfigurerSupport {
    private final RedisCacheManager redisCacheManager;

    @Override
    public CacheManager cacheManager() {
        return redisCacheManager;
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return ICacheErrorHandler.getInstance();
    }

    @Override
    public KeyGenerator keyGenerator() {
        return IKeyGenerator.getInstance();
    }

    @Override
    public CacheResolver cacheResolver() {
        return super.cacheResolver();
    }
}
