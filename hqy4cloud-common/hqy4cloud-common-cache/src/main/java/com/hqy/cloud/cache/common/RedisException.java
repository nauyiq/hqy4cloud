package com.hqy.cloud.cache.common;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
public class RedisException extends RuntimeException {

    public RedisException(String message) {
        super(message);
    }

    public RedisException(String message, Throwable cause) {
        super(message, cause);
    }

}
