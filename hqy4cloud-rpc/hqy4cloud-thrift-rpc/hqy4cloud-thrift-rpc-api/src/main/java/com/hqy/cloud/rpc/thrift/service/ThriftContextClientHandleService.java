package com.hqy.cloud.rpc.thrift.service;

import com.hqy.cloud.rpc.thrift.support.ThriftContext;

/**
 * client handler processor.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/20 14:10
 */
public interface ThriftContextClientHandleService {

    /**
     * do preWrite
     * @param thriftContext {@link ThriftContext}
     * @param methodName    method name
     * @param args          rpc request parameters.
     */
    default void doPreWrite(ThriftContext thriftContext, String methodName, Object[] args)  {

    }

    /**
     * do postWrite
     * @param thriftContext {@link ThriftContext}
     * @param methodName    method name
     * @param args          rpc request parameters.
     */
    default void doPostWrite(ThriftContext thriftContext, String methodName, Object[] args) {}

    /**
     * do preRead
     * @param thriftContext {@link ThriftContext}
     * @param methodName    method name
     */
    default void doPreRead(ThriftContext thriftContext, String methodName) {}

    /**
     * do postRead.
     * @param thriftContext {@link ThriftContext}
     * @param methodName    method name
     * @param result        rpc result.
     */
    default void doPostRead(ThriftContext thriftContext, String methodName, Object result) {}

    /**
     * done.
     * @param thriftContext {@link ThriftContext}
     * @param methodName    method name
     */
    default void doDone(ThriftContext thriftContext, String methodName) {}

    /**
     * 是否抛出异常
     * @return result
     */
    default boolean isThrowException() { return false; }

}
