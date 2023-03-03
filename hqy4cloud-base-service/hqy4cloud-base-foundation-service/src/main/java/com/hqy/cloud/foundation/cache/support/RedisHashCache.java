package com.hqy.cloud.foundation.cache.support;

import org.apache.commons.collections4.MapUtils;
import org.redisson.RedissonMapCache;
import org.redisson.api.RedissonClient;

import java.util.*;

/**
 * RedisHashCacheService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 16:11
 */
public abstract class RedisHashCache<T, PK> extends RedisCacheTemplate<T, PK> {

    private final RedissonMapCache<PK, T> redissonMapCache;

    public RedisHashCache(String project, String prefix, RedissonClient redissonClient) {
        super(project);
        this.redissonMapCache = ( RedissonMapCache<PK, T>) redissonClient.getMapCache(getRedisPrefix(prefix));
    }

    @Override
    protected List<T> getCachesFromRedis(List<PK> pks) {
        Map<PK, T> cacheAll = redissonMapCache.getAll(new LinkedHashSet<>(pks));
        if (MapUtils.isEmpty(cacheAll)) {
            return null;
        }
        return new ArrayList<>(cacheAll.values());
    }

    @Override
    public void cache(Map<PK, T> cacheMap) {
        if (MapUtils.isEmpty(cacheMap)) {
            return;
        }
        redissonMapCache.putAll(cacheMap);
    }

    @Override
    public List<T> allCaches() {
        Map<PK, T> pktMap = allCache2Map();
        if (MapUtils.isEmpty(pktMap)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(pktMap.values());
    }

    @Override
    public Map<PK, T> allCache2Map() {
        return redissonMapCache.readAllMap();
    }


    @Override
    public void invalidAll() {
        redissonMapCache.clear();
    }

    @Override
    protected void saveCache2Redis(PK pk, T cache) {
        redissonMapCache.put(pk, cache);
    }

    @Override
    protected T getCacheFromRedis(PK pk) {
        return redissonMapCache.get(pk);
    }

    @Override
    protected void invalidCacheFromRedis(PK pk) {
        redissonMapCache.remove(pk);
    }


    private String getRedisPrefix(String prefix) {
        return getGenerator().genPrefix(prefix);
    }

}
