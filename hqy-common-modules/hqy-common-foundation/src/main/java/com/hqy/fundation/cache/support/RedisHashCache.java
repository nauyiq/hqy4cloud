package com.hqy.fundation.cache.support;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hqy.base.common.base.lang.BaseMathConstants;
import com.hqy.fundation.cache.redis.LettuceRedis;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * RedisHashCacheService.
 * 对应的实体需要加注解 @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 16:11
 */
public abstract class RedisHashCache<T, PK> extends RedisCacheTemplate<T, PK> {

    private final String prefix;

    public RedisHashCache(String project, String prefix) {
        super(project);
        this.prefix = prefix;
    }

    @Override
    protected List<T> getCachesFromRedis(List<PK> pks) {
        List<T> pkList = LettuceRedis.getInstance().hmGet(getRedisPrefix(), pks);
        boolean haveNull = pkList.stream().anyMatch(Objects::isNull);
        return haveNull ? null : pkList;
    }

    @Override
    public void cache(Map<PK, T> cacheMap) {
        if (MapUtils.isEmpty(cacheMap)) {
            return;
        }
        Map<PK, T> newCacheMap = cacheMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        LettuceRedis.getInstance().hmSet(getRedisPrefix(), newCacheMap);
    }

    @Override
    public List<T> allCaches() {
        Map<PK, T> pktMap = allCache2Map();
        if (pktMap.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(pktMap.values());
    }

    @Override
    public Map<PK, T> allCache2Map() {
        Map<String, T> map = LettuceRedis.getInstance().hGetAll(getRedisPrefix());
        return map.entrySet().stream().collect(Collectors.toMap(e -> StringConvertPkType(e.getKey()), Map.Entry::getValue));
    }

    public abstract PK StringConvertPkType(String pkStr);

    @Override
    public void invalidAll() {
        LettuceRedis.getInstance().del(getRedisPrefix());
    }

    @Override
    protected void saveCache2Redis(PK pk, T cache) {
        LettuceRedis.getInstance().hSet(getRedisPrefix(), pk, cache, BaseMathConstants.ONE_DAY_4MILLISECONDS, TimeUnit.MILLISECONDS);
    }

    @Override
    protected T getCacheFromRedis(PK pk) {
        return LettuceRedis.getInstance().hGet(getRedisPrefix(), pk);
    }

    @Override
    protected void invalidCacheFromRedis(PK pk) {
        LettuceRedis.getInstance().hDel(getRedisPrefix(), pk);
    }


    private String getRedisPrefix() {
        return getGenerator().genPrefix(prefix);
    }

}
