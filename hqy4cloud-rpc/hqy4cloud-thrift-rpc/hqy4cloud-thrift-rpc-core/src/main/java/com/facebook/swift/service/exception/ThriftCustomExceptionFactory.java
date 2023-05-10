package com.facebook.swift.service.exception;

import com.facebook.swift.service.exception.support.ThriftCustomException;

/**
 * create ThriftCustomException.
 * @see ThriftCustomException
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/9 16:03
 */
public interface ThriftCustomExceptionFactory {


    /**
     * createException
     * @param id      exception id
     * @param message exception message
     * @param cause   exception cause
     * @return        {@link ThriftCustomException}
     */
    ThriftCustomException createException(int id, String message, Throwable cause);

}
