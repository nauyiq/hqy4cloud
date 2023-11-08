package com.hqy.cloud.util.crypto.symmetric;

import com.hqy.cloud.util.crypto.CryptoType;

/**
 * Symmetric.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/15 13:31
 */
public interface Symmetric {
    long DEFAULT_EXPIRED_SECONDS = 60 * 60;

    CryptoType getType();

    String getKey();

    default  <T> String encrypt(T data) {
        return encrypt(data, DEFAULT_EXPIRED_SECONDS);
    }
    <T> String encrypt(T data, long expiredSeconds);

    default String decrypt(String encryptContent) {
        return decrypt(encryptContent, null);
    }

    <T> T decrypt(String encryptContent, Class<T> clazz);

    boolean isExpired(String encryptContent);


}
