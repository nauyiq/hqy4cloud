package com.hqy.fundation.cache.support;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.fundation.cache.CacheService;
import com.hqy.util.AssertUtil;
import com.hqy.util.ReflectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 基于redis的缓存service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 15:10
 */
@Slf4j
public abstract class RedisCacheTemplate<T, PK> implements CacheService<T, PK> {

    private String redisPrefix;
    private int delaySeconds = 1;

    RedisCacheTemplate() {
    }

    public RedisCacheTemplate(String redisPrefix) {
        this.redisPrefix = redisPrefix;
    }

    @Override
    public final T getCache(PK pk) {
        AssertUtil.notNull(pk, "Cache pk should not be null.");
        T cache = getCacheFromRedis(pk);
        if (cache == null) {
            cache = getCacheFromDb(pk);
            if (cache != null) {
                saveCache2Redis(pk, cache);
            }
        }
        return cache;
    }

    @Override
    public final List<T> getCaches(List<PK> pks) {
        if (CollectionUtils.isEmpty(pks)) {
            return Collections.emptyList();
        }
        List<T> caches = getCachesFromRedis(pks);
        if (CollectionUtils.isEmpty(caches)) {
            caches = getCachesFromDb(pks);
            if (CollectionUtils.isNotEmpty(caches)) {
                HashMap<PK, T> map = MapUtil.newHashMap(pks.size());
                for (int i = 0; i < pks.size(); i++) {
                    map.put(pks.get(i), caches.get(i));
                }
                cache(map);
            }
        }
        return caches;
    }


    @Override
    public final void cache(PK pk, T cache) {
        AssertUtil.notNull(cache, "Cache should not be null.");
        saveCache2Redis(pk, cache);
    }


    @Override
    public final void update(PK pk, T cache) {
        AssertUtil.notNull(cache, "Cache should not be null.");
        AssertUtil.notNull(pk, "Cache pk should not be null.");
        //延迟双删
        invalid(pk);
        updateDb(pk, cache);
        try {
            TimeUnit.SECONDS.sleep(delaySeconds);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        invalid(pk);
    }


    @Override
    public final void invalid(PK pk) {
        invalidCacheFromRedis(pk);
    }

    protected abstract List<T> getCachesFromDb(List<PK> pks);

    protected abstract void saveCache2Redis(PK pk, T cache);

    protected abstract T getCacheFromDb(PK pk);

    protected abstract List<T> getCachesFromRedis(List<PK> pks);

    protected abstract T getCacheFromRedis(PK pk);

    protected abstract void updateDb(PK pk, T cache);

    protected abstract void invalidCacheFromRedis(PK pk);

    public String getRedisPrefix() {
        if (StringUtils.isBlank(redisPrefix)) {
            redisPrefix = ReflectUtils.getTargetGenericClass(getClass(), 0).getSimpleName();
        }
        return redisPrefix;
    }

    public String genKey(PK pk) {
        return getRedisPrefix() + StringConstants.Symbol.COLON + pk.toString();
    }

    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }



}
