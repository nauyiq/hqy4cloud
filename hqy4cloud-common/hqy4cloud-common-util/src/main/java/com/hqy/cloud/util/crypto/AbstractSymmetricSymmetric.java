package com.hqy.cloud.util.crypto;

import com.hqy.cloud.util.crypto.symmetric.Symmetric;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/15 13:34
 */
public abstract class AbstractSymmetricSymmetric implements Symmetric {
    private final String key;
    public AbstractSymmetricSymmetric(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
}
