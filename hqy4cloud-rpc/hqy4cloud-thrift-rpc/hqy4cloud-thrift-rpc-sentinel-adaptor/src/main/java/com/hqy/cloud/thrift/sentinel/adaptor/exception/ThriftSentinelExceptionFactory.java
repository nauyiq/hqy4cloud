package com.hqy.cloud.thrift.sentinel.adaptor.exception;

import com.facebook.swift.service.exception.ThriftCustomExceptionFactory;
import com.facebook.swift.service.exception.support.ThriftCustomException;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/9 16:19
 */
public class ThriftSentinelExceptionFactory implements ThriftCustomExceptionFactory {

    @Override
    public ThriftCustomException createException(int id, String message, Throwable cause) {
        return new ThriftSentinelBlockException(id, message, cause);
    }
}
