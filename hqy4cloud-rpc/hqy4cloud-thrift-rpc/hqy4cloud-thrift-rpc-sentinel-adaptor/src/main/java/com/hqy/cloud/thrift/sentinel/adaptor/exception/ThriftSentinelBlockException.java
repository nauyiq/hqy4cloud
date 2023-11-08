package com.hqy.cloud.thrift.sentinel.adaptor.exception;

import com.facebook.swift.service.exception.support.ThriftCustomException;

/**
 * ThriftSentinelBlockException.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/6 18:03
 */
public class ThriftSentinelBlockException extends ThriftCustomException {
    public static final int ID = 16;

    public ThriftSentinelBlockException(int type, Throwable cause) {
        super(type, cause);
    }

    public ThriftSentinelBlockException(int type, String message, Throwable cause) {
        super(type, message, cause);
    }



}
