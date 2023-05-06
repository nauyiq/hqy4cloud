package com.hqy.cloud.thrift.sentinel.adaptor.exception;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/6 18:03
 */
public class SentinelBlockException extends RuntimeException {

    public SentinelBlockException(Throwable cause) {
        super(cause);
    }
}
