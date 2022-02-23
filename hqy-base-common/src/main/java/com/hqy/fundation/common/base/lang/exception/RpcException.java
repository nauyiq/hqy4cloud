package com.hqy.fundation.common.base.lang.exception;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/2/23 17:16
 */
public class RpcException extends RuntimeException{

    private static final long serialVersionUID = -2907275534434683708L;

    public RpcException() {
        super();
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
