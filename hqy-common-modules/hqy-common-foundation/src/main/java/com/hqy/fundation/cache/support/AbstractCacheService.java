package com.hqy.fundation.cache.support;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.fundation.cache.CacheConfig;
import com.hqy.fundation.cache.CacheService;
import com.hqy.fundation.cache.redis.LettuceRedis;
import com.hqy.util.AssertUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * AbstractCacheService.
 * 基于google guava cache的缓存
 * 可根据配置引导类判断是否采用redis作为缓存service的二级缓存
 * redis采用的数据结构为map... 因此务必严谨使用redis map... 从而导致redis内存溢出
 * 如果是在分布式环境下使用， 对缓存一致性要求严格的话。。。 不建议推荐使用service。。。建议使用 {@link RedisCacheTemplate}.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 13:34
 */
public abstract class AbstractCacheService<T, PK> implements CacheService<T, PK> {
    private static final Logger log = LoggerFactory.getLogger(AbstractCacheService.class);

    protected final Cache<PK, T> cached;
    protected final boolean usingRedis;
    private String redisKey;

    public AbstractCacheService(CacheConfig config) {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder()
                //设置并发级别为8，并发级别是指可以同时写缓存的线程数
                .concurrencyLevel(config.getConcurrencyLevel())
                //设置缓存容器的初始容量
                .initialCapacity(config.getInitialCapacity())
                //设置缓存最大容量，超过最大容量之后就会按照LRU最近虽少使用算法来移除缓存项
                .maximumSize(config.getMaximumSize())
                //设置写缓存后n秒钟过期
                .expireAfterWrite(config.getExpiredSeconds(), TimeUnit.SECONDS);
        if (config.isWeakKey()) {
            //采用弱引用key
            builder = builder.weakKeys();
        }
        if (config.isWeakValues()) {
            //采用弱引用value
            builder = builder.weakValues();
        }
        cached = builder.build();
        usingRedis = config.isUsingRedis();
        if (usingRedis) {
            log.info("Start cache service, using redis cache.");
            redisKey = getClass().getSimpleName() + StringConstants.Symbol.COLON;
        }
    }


    @Override
     public T getCache(PK pk) {
        AssertUtil.notNull(pk, "Cache key should not be null.");
        T cache = cached.getIfPresent(pk);
        if (cache == null) {
            if (usingRedis) {
                cache = LettuceRedis.getInstance().hGet(redisKey, pk.toString());
            }
            if (cache == null) {
                cache = getCacheFromDb(pk);
                if (cache != null) {
                    cache(pk, cache);
                }
            } else {
                cached.put(pk, cache);
            }
        }
        return cache;
    }

    @Override
    public List<T> getCaches(List<PK> pks) {
        if (CollectionUtils.isEmpty(pks)) {
            return Collections.emptyList();
        }
        return pks.stream().map(this::getCache).collect(Collectors.toList());
    }

    @Override
    public void cache(Map<PK, T> cacheMap) {
        if (MapUtils.isEmpty(cacheMap)) {
            return;
        }
        for (Map.Entry<PK, T> entry : cacheMap.entrySet()) {
            PK key = entry.getKey();
            T value = entry.getValue();
            try {
                cache(key, value);
            } catch (Exception cause) {
                log.error("Failed execute to cache, pk:{}, cause:{}", key, cause.getMessage());
            }

        }
    }

    /**
     * 从数据库或者其他数据源中获取缓存数据
     * @param pk 主键或者唯一标识
     * @return   Cache
     */
    public abstract T getCacheFromDb(PK pk);


    @Override
    public void cache(PK pk, T cache) {
        AssertUtil.notNull(cache, "Cache should not be null.");
        AssertUtil.notNull(pk, "Cache pk should not be null.");
        cached.put(pk, cache);
        LettuceRedis.getInstance().hSet(redisKey, pk.toString(), cache);
    }



    @Override
    public  void update(PK pk, T cache) {
        this.cache(pk, cache);
    }

    @Override
    public Set<T> allCache2Set() {
        Map<PK, T> pkCacheMap = allCache2Map();
        if (pkCacheMap == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(pkCacheMap.values());
    }

    @Override
    public  Map<PK, T> allCache2Map() {
        if (cached.size() != 0) {
            return cached.asMap();
        }
        if (usingRedis) {
            return LettuceRedis.getInstance().hGetAll(redisKey);
        }
        return getAllCacheFromDb();
    }

    /**
     * 从数据库或者其他数据源中获取所有需要缓存的数据
     * @return 所有缓存
     */
    protected abstract Map<PK, T> getAllCacheFromDb();


    @Override
    public void invalid(PK pk) {
        AssertUtil.notNull(pk, "Cache pk should not be null.");
        cached.invalidate(pk);
        if (usingRedis) {
            LettuceRedis.getInstance().hDel(redisKey, pk.toString());
        }
    }

    @Override
    public void invalidAll() {
        cached.invalidateAll();
        if (usingRedis) {
            LettuceRedis.getInstance().del(redisKey);
        }
    }
}
