package com.hqy.cloud.lock.common;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/19
 */
public class DistributeLockException extends RuntimeException {

    public DistributeLockException(String message) {
        super(message);
    }

    public DistributeLockException(String message, Throwable cause) {
        super(message, cause);
    }
}
