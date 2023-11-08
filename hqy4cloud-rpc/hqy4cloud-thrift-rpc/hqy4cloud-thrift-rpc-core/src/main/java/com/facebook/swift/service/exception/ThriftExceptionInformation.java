package com.facebook.swift.service.exception;

import com.facebook.swift.service.exception.support.ThriftCustomException;
import org.apache.thrift.TApplicationException;

/**
 * 自定义thrift异常信息
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/9 11:26
 */
public interface ThriftExceptionInformation {

    /**
     * 异常id， 根据业务进行区分，建议id值从16开始进行设计 详细参考{@link TApplicationException#getType()}
     * @return id
     */
    int getId();

    /**
     * 异常class类型
     * @return class type.
     */
    Class<? extends ThriftCustomException> getExceptionType();


    /***
     * get create exception factory.
     * @return {@link ThriftCustomExceptionFactory}
     */
    ThriftCustomExceptionFactory getFactory();




}
