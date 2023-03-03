package com.hqy.cloud.foundation.cache.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.foundation.cache.CacheService;
import com.hqy.cloud.foundation.cache.redis.key.support.DefaultKeyGenerator;
import com.hqy.cloud.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 基于redis的缓存service
 * 使用redis做缓存的时候，务必考虑缓存命中和缓存大小的问题
 * 使用的业务应该是比较热点的数据，因此采用缓存机制...
 * 如果请求的资源比较冷门 或热点数据对应数据库资源不存在，可能导致以下问题：
 * 1. 冷数据存储在redis中，但并不怎么被访问，特别hash结构时可能导致冷数据一直不过期
 * 2. 当前service redis没有存储空值，意味着每次查询如果从redis中查出来为空则会查询db或其他数据源... 导致缓存命中问题... 即如果资源为空时，
 * 大请求会走redis再走db 导致系统压力反而增大，并且吞吐量降低。 因此在业务中使用此service的时候，应该考虑业务中的实际情况 合理使用。
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 15:10
 */
@Slf4j
public abstract class RedisCacheTemplate<T, PK> implements CacheService<T, PK> {

    private final DefaultKeyGenerator generator;
    private int delaySeconds = 1;

    public RedisCacheTemplate(String project) {
        this.generator = new DefaultKeyGenerator(project);
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
        //TODO 没有缓存空值， 缓存命中问题???
        List<T> caches = getCachesFromRedis(pks);
        if (CollectionUtils.isEmpty(caches)) {
            caches = getCachesFromDb(pks);
            if (CollectionUtils.isNotEmpty(caches)) {
                HashMap<PK, T> map = MapUtil.newHashMap(caches.size());
                for (int i = 0; i < caches.size(); i++) {
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

    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    public DefaultKeyGenerator getGenerator() {
        return generator;
    }
}
