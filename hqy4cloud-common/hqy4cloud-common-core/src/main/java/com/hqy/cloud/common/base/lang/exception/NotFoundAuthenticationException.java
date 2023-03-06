package com.hqy.cloud.common.base.lang.exception;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/6 10:17
 */
public class NotFoundAuthenticationException extends RuntimeException {

    public NotFoundAuthenticationException(String message) {
        super(message);
    }

    public NotFoundAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundAuthenticationException(Throwable cause) {
        super(cause);
    }
}
