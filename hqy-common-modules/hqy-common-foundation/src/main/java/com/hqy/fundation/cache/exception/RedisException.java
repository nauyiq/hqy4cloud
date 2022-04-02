package com.hqy.fundation.cache.exception;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/2 15:14
 */
public class RedisException extends RuntimeException {

    private static final long serialVersionUID = 8032905067046767776L;

    public RedisException() {
        super();
    }

    public RedisException(String message) {
        super(message);
    }

    public RedisException(String message, Throwable cause) {
        super(message, cause);
    }
}
