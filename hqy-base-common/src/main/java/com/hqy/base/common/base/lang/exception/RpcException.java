package com.hqy.base.common.base.lang.exception;

import javax.naming.LimitExceededException;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/2/23 17:16
 */
public class RpcException extends RuntimeException{

    public static final int UNKNOWN_EXCEPTION = 0;
    public static final int NETWORK_EXCEPTION = 1;
    public static final int TIMEOUT_EXCEPTION = 2;
    public static final int BIZ_EXCEPTION = 3;
    public static final int FORBIDDEN_EXCEPTION = 4;
    public static final int SERIALIZATION_EXCEPTION = 5;
    public static final int NO_INVOKER_AVAILABLE_AFTER_FILTER = 6;
    public static final int LIMIT_EXCEEDED_EXCEPTION = 7;
    public static final int TIMEOUT_TERMINATE = 8;
    public static final int REGISTRY_EXCEPTION = 9;
    public static final int ROUTER_CACHE_NOT_BUILD = 10;
    public static final int METHOD_NOT_FOUND = 11;
    public static final int VALIDATION_EXCEPTION = 12;
    private static final long serialVersionUID = -2907275534434683708L;

    /**
     * RpcException cannot be extended, use error code for exception type to keep compatibility
     */
    private int code;

    public RpcException() {
        super();
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(int code) {
        super();
        this.code = code;
    }

    public RpcException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public RpcException(int code, String message) {
        super(message);
        this.code = code;
    }

    public RpcException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isBiz() {
        return code == BIZ_EXCEPTION;
    }

    public boolean isForbidden() {
        return code == FORBIDDEN_EXCEPTION;
    }

    public boolean isTimeout() {
        return code == TIMEOUT_EXCEPTION;
    }

    public boolean isNetwork() {
        return code == NETWORK_EXCEPTION;
    }

    public boolean isSerialization() {
        return code == SERIALIZATION_EXCEPTION;
    }

    public boolean isNoInvokerAvailableAfterFilter() {
        return code == NO_INVOKER_AVAILABLE_AFTER_FILTER;
    }

    public boolean isLimitExceed() {
        return code == LIMIT_EXCEEDED_EXCEPTION || getCause() instanceof LimitExceededException;
    }

    public boolean isValidation() {
        return code == VALIDATION_EXCEPTION;
    }

}
