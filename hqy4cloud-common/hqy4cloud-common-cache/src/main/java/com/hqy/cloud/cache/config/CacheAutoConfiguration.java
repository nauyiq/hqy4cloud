package com.hqy.cloud.cache.config;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.hqy.cloud.util.config.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 缓存配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
@Configuration
//@EnableCaching
//@EnableConfigurationProperties(value = CacheProperties.class)
@EnableMethodCache(basePackages = "com.hqy.cloud")
@PropertySource(value = "classpath:cache.yml", factory = YamlPropertySourceFactory.class)
public class CacheAutoConfiguration {

    /*@Bean(name = RedisConstants.DEFAULT_KEY_GENERATOR_NAME)
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
    }*/




}
