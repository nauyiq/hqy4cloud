package com.hqy.foundation.cache;

import com.hqy.base.common.base.lang.BaseStringConstants;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/31 13:46
 */
public abstract class AbstractPrefix implements Prefix {

    /**
     * 过期时间 单位秒
     */
    private final int expireSeconds;

    /**
     * 前缀
     */
    private final String prefix;

    public AbstractPrefix(String prefix) {
        this(0, prefix);
    }

    public AbstractPrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String prefix() {
       return getClass().getSimpleName().concat(BaseStringConstants.Symbol.COLON).concat(prefix);
    }
}

