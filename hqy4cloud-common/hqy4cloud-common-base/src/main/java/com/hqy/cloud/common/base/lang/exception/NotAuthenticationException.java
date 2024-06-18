package com.hqy.cloud.common.base.lang.exception;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/6 10:17
 */
public class NotAuthenticationException extends RuntimeException {

    public NotAuthenticationException(String message) {
        super(message);
    }

    public NotAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAuthenticationException(Throwable cause) {
        super(cause);
    }
}
