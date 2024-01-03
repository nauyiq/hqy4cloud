package com.hqy.cloud.registry.common.exeception;

/**
 * RegistryException.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public class RegisterDiscoverException extends RuntimeException {
    private int code;

    public RegisterDiscoverException(String message) {
        super(message);
    }

    public RegisterDiscoverException(int code, String message) {
        super(message);
        this.code = code;
    }

    public RegisterDiscoverException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegisterDiscoverException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
