package com.hqy.foundation.cache;

import com.hqy.base.common.base.lang.BaseStringConstants;

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
        this(0, key);
    }

    public CacheKey(int expireSeconds, String key) {
        this.expireSeconds = expireSeconds;
        this.key = key;
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String key() {
       return getClass().getSimpleName().concat(BaseStringConstants.Symbol.COLON).concat(key);
    }
}

