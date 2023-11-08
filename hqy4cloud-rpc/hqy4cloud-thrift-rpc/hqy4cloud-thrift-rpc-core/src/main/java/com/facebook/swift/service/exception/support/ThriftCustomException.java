package com.facebook.swift.service.exception.support;

import org.apache.thrift.TApplicationException;

/**
 * thrift自定义异常类，业务层如果想要服务端抛出指定的异常必须继承该类.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/9 15:03
 */
public abstract class ThriftCustomException extends TApplicationException {
    private final Throwable cause;

    public ThriftCustomException(int type, Throwable cause) {
        super(type);
        this.cause = cause;
    }

    public ThriftCustomException(int type, String message, Throwable cause) {
        super(type, message);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }




}
