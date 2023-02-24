package com.hqy.fundation.cache;

import com.hqy.cloud.common.base.lang.NumberConstants;
import lombok.Data;

/**
 * Setting.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 11:30
 */
@Data
public class CacheConfig {

    /**
     * 写缓存并发级别
     */
    private int concurrencyLevel;

    /**
     * 初始大小
     */
    private int initialCapacity;

    /**
     * 最大容量
     */
    private long maximumSize;

    /**
     * 过期时间
     */
    private long expiredSeconds;

    /**
     * 是否软引用key
     */
    private boolean isWeakKey;

    /**
     * 是否软引用value
     */
    private boolean isWeakValues;

    /**
     * 是否采用redis做二级缓存
     */
    private boolean isUsingRedis;


    public CacheConfig() {
        this(NumberConstants.ONE_DAY_4MILLISECONDS);
    }

    public CacheConfig(long expiredSeconds) {
        this(8, 100, 1024, expiredSeconds);
    }

    public CacheConfig(int concurrencyLevel, int initialCapacity, long maximumSize, long expiredSeconds) {
        this(concurrencyLevel, initialCapacity, maximumSize, expiredSeconds, false, false, true);
    }

    public CacheConfig(int concurrencyLevel, int initialCapacity, long maximumSize, long expiredSeconds, boolean isWeakKey, boolean isWeakValues, boolean isUsingRedis) {
        this.concurrencyLevel = concurrencyLevel;
        this.initialCapacity = initialCapacity;
        this.maximumSize = maximumSize;
        this.expiredSeconds = expiredSeconds;
        this.isWeakKey = isWeakKey;
        this.isWeakValues = isWeakValues;
        this.isUsingRedis = isUsingRedis;
    }
}
