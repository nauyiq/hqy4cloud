package com.hqy.cloud.thrift.sentinel.adaptor.exception;

import com.facebook.swift.service.exception.ThriftCustomExceptionFactory;
import com.facebook.swift.service.exception.support.BaseThriftExceptionInformation;
import com.facebook.swift.service.exception.support.ThriftCustomException;

/**
 * ThriftSentinelBlockExceptionInformation.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/9 11:35
 */
public class ThriftSentinelBlockExceptionInformation extends BaseThriftExceptionInformation {

    @Override
    public int getId() {
        return ThriftSentinelBlockException.ID;
    }

    @Override
    public Class<? extends ThriftCustomException> getExceptionType() {
        return ThriftSentinelBlockException.class;
    }

    @Override
    protected ThriftCustomExceptionFactory createFactory() {
        return new ThriftSentinelExceptionFactory();
    }
}
