package com.hqy.foundation.cache;

import com.hqy.base.common.base.lang.StringConstants;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/31 13:46
 */
public class CacheKey implements Key {

    /**
     * 过期时间 单位秒
     */
    private final int expireSeconds;

    /**
     * 前缀
     */
    private final String key;

    public CacheKey(String key) {
        this(key, 0);
    }

    public CacheKey(String key, int expireSeconds) {
        this.expireSeconds = expireSeconds;
        this.key = key;
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String key() {
       return getClass().getSimpleName().concat(StringConstants.Symbol.COLON).concat(key);
    }
}

