package com.hqy.fundation.cache.support;

import com.hqy.fundation.cache.redis.LettuceRedis;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RedisHashCacheService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 16:11
 */
public abstract class RedisHashCache<T, PK> extends RedisCacheTemplate<T, PK> {


    @Override
    protected List<T> getCachesFromRedis(List<PK> pks) {
        List<String> strings = pks.stream().map(PK::toString).collect(Collectors.toList());
        List<T> pkList = LettuceRedis.getInstance().hmGet(redisPrefix, strings);
        boolean haveNull = pkList.stream().anyMatch(Objects::isNull);
        return haveNull ? null : pkList;
    }

    @Override
    public void cache(Map<PK, T> cacheMap) {
        if (MapUtils.isEmpty(cacheMap)) {
            return;
        }
        LettuceRedis.getInstance().hmSet(redisPrefix, cacheMap);
    }

    @Override
    public Set<T> allCache2Set() {
        return super.allCache2Set();
    }

    @Override
    public Map<PK, T> allCache2Map() {
        return LettuceRedis.getInstance().hGetAll(redisPrefix);
    }

    @Override
    public void invalidAll() {
        LettuceRedis.getInstance().del(redisPrefix);
    }

    @Override
    protected void saveCache2Redis(PK pk, T cache) {
        LettuceRedis.getInstance().hSet(redisPrefix, pk.toString(), cache);
    }

    @Override
    protected T getCacheFromRedis(PK pk) {
        return LettuceRedis.getInstance().hGet(redisPrefix, pk.toString());
    }

    @Override
    protected void invalidCacheFromRedis(PK pk) {
        LettuceRedis.getInstance().hDel(redisPrefix, pk.toString());
    }



}
