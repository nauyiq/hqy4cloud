package com.facebook.swift.service.exception.support;

import com.facebook.swift.service.exception.ThriftCustomExceptionFactory;
import com.facebook.swift.service.exception.ThriftExceptionInformation;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/9 16:16
 */
public abstract class BaseThriftExceptionInformation implements ThriftExceptionInformation {
    private volatile ThriftCustomExceptionFactory factory;

    @Override
    public ThriftCustomExceptionFactory getFactory() {
        if (factory == null) {
            synchronized (getExceptionType()) {
                if (factory == null) {
                    factory = createFactory();
                }
            }
        }
        return factory;
    }

    /**
     * create factory
     * @return {@link ThriftCustomExceptionFactory}
     */
    protected abstract ThriftCustomExceptionFactory createFactory();


}
