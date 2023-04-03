package com.hqy.cloud.foundation.cache.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.foundation.cache.CacheDataService;
import com.hqy.cloud.foundation.cache.redis.key.RedisKey;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
@RequiredArgsConstructor
public abstract class RedisCacheDataService<T, PK> implements CacheDataService<T, PK> {

    private final RedisKey redisKey;
    private int delayMs = 500;

    @Override
    public final T getData(PK pk) {
        AssertUtil.notNull(pk, "Cache pk should not be null.");
        T cache = getCacheFromRedis(pk);
        if (Objects.isNull(cache)) {
            cache = getDataBySource(pk);
            if (Objects.nonNull(cache)) {
                cache(pk, cache);
            }
        }
        return cache;
    }

    @Override
    public final List<T> getData(List<PK> pks) {
        if (CollectionUtils.isEmpty(pks)) {
            return Collections.emptyList();
        }
        List<T> caches = getCacheFromRedis(pks);
        if (CollectionUtils.isEmpty(caches)) {
            caches = getDataBySource(pks);
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
    public final void update(PK pk, T cache) {
        AssertUtil.notNull(cache, "Cache should not be null.");
        AssertUtil.notNull(pk, "Cache pk should not be null.");
        //延迟双删
        invalid(pk);
        updateData(pk, cache);
        try {
            TimeUnit.MILLISECONDS.sleep(delayMs);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        invalid(pk);
    }


    protected T getCacheFromRedis(PK pk) {
        List<T> cacheFromRedis = getCacheFromRedis(Collections.singletonList(pk));
        if (CollectionUtils.isEmpty(cacheFromRedis)) {
            return null;
        }
        return cacheFromRedis.get(0);
    }

    protected T getDataBySource(PK pk) {
        List<T> dataBySource = getDataBySource(Collections.singletonList(pk));
        if (CollectionUtils.isEmpty(dataBySource)) {
            return null;
        }
        return dataBySource.get(0);
    }

    /**
     * 从redis中获取数据
     * @param pks 主键key
     * @return     data.
     */
    protected abstract List<T> getCacheFromRedis(List<PK> pks);

    /**
     * 从数据源从获取数据
     * @param pks 主键.
     * @return    data.
     */
    protected abstract List<T> getDataBySource(List<PK> pks);

    /**
     * 更新数据
     * @param pk    主键
     * @param cache 缓存的数据
     * @return      result
     */
    protected abstract boolean updateData(PK pk, T cache);

    public void setDelayMs(int delayMs) {
        this.delayMs = delayMs;
    }

    public RedisKey getRedisKey() {
        return redisKey;
    }
}
