package com.hqy.cloud.cache.autoconfigure;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.hqy.cloud.cache.common.IKeyGenerator;
import com.hqy.cloud.cache.common.RedisConstants;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 缓存配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(value = CacheProperties.class)
@EnableMethodCache(basePackages = "com.hqy.cloud")
public class CacheAutoConfiguration {

    @Bean(name = RedisConstants.DEFAULT_KEY_GENERATOR_NAME)
    public KeyGenerator keyGenerator() {
        return new IKeyGenerator();
    }

    @Primary
    @Bean(name = RedisConstants.REDIS_CACHE_MANAGE)
    public RedisCacheManager redisCacheManager(CacheProperties cacheProperties,
                                               RedisTemplate<String, Object> redisTemplate,
                                               ObjectProvider<CacheManagerCustomizer<?>> customizers,
                                               ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers) {
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        // 写配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getValueSerializer()));
        config = config.entryTtl(redisProperties.getTimeToLive() == null ? RedisConstants.CACHE_DEFAULT_EXPIRE : redisProperties.getTimeToLive());
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(factory)
                .cacheDefaults(config);
        List<String> cacheNames = cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            builder.initialCacheNames(new LinkedHashSet<>(cacheNames));
        }
        if (redisProperties.isEnableStatistics()) {
            builder.enableStatistics();
        }
        redisCacheManagerBuilderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        CacheManagerCustomizers cacheManagerCustomizers = new CacheManagerCustomizers(customizers.orderedStream().collect(Collectors.toList()));
        return cacheManagerCustomizers.customize(builder.build());
    }

    @Bean(name = RedisConstants.CAFFEINE_CACHE_MANAGER)
    public CaffeineCacheManager caffeineCacheManager(CacheProperties cacheProperties,
                                                     ObjectProvider<CacheManagerCustomizer<?>> customizers,
                                                     ObjectProvider<Caffeine<Object, Object>> caffeine,
                                                     ObjectProvider<CaffeineSpec> caffeineSpec,
                                                     ObjectProvider<CacheLoader<Object, Object>> cacheLoader) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        setCacheBuilder(cacheProperties, caffeineSpec.getIfAvailable(), caffeine.getIfAvailable(), cacheManager);
        cacheLoader.ifAvailable(cacheManager::setCacheLoader);
        return cacheManager;
    }

    private void setCacheBuilder(CacheProperties cacheProperties, CaffeineSpec caffeineSpec,
                                 Caffeine<Object, Object> caffeine, CaffeineCacheManager cacheManager) {
        String specification = cacheProperties.getCaffeine().getSpec();
        if (StringUtils.hasText(specification)) {
            cacheManager.setCacheSpecification(specification);
        }
        else if (caffeineSpec != null) {
            cacheManager.setCaffeineSpec(caffeineSpec);
        }
        else if (caffeine != null) {
            cacheManager.setCaffeine(caffeine);
        }
    }




}
