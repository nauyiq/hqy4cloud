package com.hqy.cloud.foundation.cache.support;

import com.hqy.cloud.foundation.cache.redis.key.RedisKey;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public abstract class RedisHashCacheDataService<T, PK> extends RedisCacheDataService<T, PK> {
    private final RedissonMapCache<PK, T> redissonMapCache;

    public RedisHashCacheDataService(RedisKey redisKey, RedissonClient redissonClient) {
        super(redisKey);
        this.redissonMapCache = ( RedissonMapCache<PK, T>) redissonClient.getMapCache(redisKey.getKey());
    }

    @Override
    protected List<T> getCacheFromRedis(List<PK> pks) {
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
    public List<T> getAllData() {
        Map<PK, T> pktMap = getAllDataMap();
        if (MapUtils.isEmpty(pktMap)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(pktMap.values());
    }

    @Override
    public Map<PK, T> getAllDataMap() {
        return redissonMapCache.readAllMap();
    }

    @Override
    public void invalidAll() {
        redissonMapCache.clear();
    }


    @Override
    protected T getCacheFromRedis(PK pk) {
        return redissonMapCache.get(pk);
    }

    @Override
    public void cache(PK pk, T cache) {
        this.redissonMapCache.put(pk, cache);
    }

    @Override
    public boolean invalid(PK pk) {
        try {
            this.redissonMapCache.remove(pk);
            return true;
        } catch (Throwable cause) {
            log.error("Failed execute to delete cache from redis.", cause);
            return false;
        }
    }
}
