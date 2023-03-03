package com.hqy.cloud.foundation.cache;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CacheService.
 * PK: 表示当前缓存的唯一标识、主键的
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 11:35
 */
public interface CacheService<T, PK> {

    /**
     * 根据唯一标识获取缓存
     * @param pk 唯一标识
     * @return cacheData
     */
    T getCache(PK pk);

    /**
     * 批量获取缓存
     * @param pks 主键列表
     * @return Cache
     */
    List<T> getCaches(List<PK> pks);

    /**
     * 保存缓存
     * @param pk    唯一标识
     * @param cache cacheData.
     */
    void cache(PK pk, T cache);

    /**
     * 批量保存缓存
     * @param cacheMap cacheMap
     */
    void cache(Map<PK, T> cacheMap);

    /**
     * 更新缓存
     * @param pk    唯一标识
     * @param cache cacheData.
     */
    void update(PK pk, T cache);


    /**
     * 获取所有的缓存
     * @return cache cacheData.
     */
    default List<T> allCaches() {
        return Collections.emptyList();
    }

    /**
     * 获取所有的缓存
     * @return cache cacheData.
     */
    default Map<PK, T> allCache2Map() {
        return Collections.emptyMap();
    }

    /**
     * 使缓存失效。
     * @param pk 唯一标识
     */
    void invalid(PK pk);

    /**
     * 使所有缓存失效
     */
    default void invalidAll() {
    }

}
