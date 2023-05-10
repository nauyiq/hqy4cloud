package com.hqy.cloud.rpc.thrift.service;

import com.hqy.cloud.rpc.thrift.support.ThriftServerContext;

/**
 * ThriftServerContextHandleService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/21 15:39
 */
public interface ThriftServerContextHandleService {

    /**
     * do preRead.
     * @param thriftServerContext {@link ThriftServerContext}
     * @param methodName           method name
     */
    default void doPreRead(ThriftServerContext thriftServerContext, String methodName) {}

    /**
     * do postRead.
     * @param thriftServerContext {@link ThriftServerContext}
     * @param methodName           method name.
     * @param args                 request args.
     */
    default void doPostRead(ThriftServerContext thriftServerContext, String methodName, Object[] args) {}

    /**
     * do preWrite.
     * @param thriftServerContext {@link ThriftServerContext}
     * @param methodName          method name.
     * @param result              rpc result.
     */
    default void doPreWrite(ThriftServerContext thriftServerContext, String methodName, Object result) {}

    /**
     * do postWrite.
     * @param thriftServerContext {@link ThriftServerContext}
     * @param methodName          method name
     * @param result              rpc result.
     */
    default void doPostWrite(ThriftServerContext thriftServerContext, String methodName, Object result) {}

    /**
     * done.
     * @param thriftServerContext {@link ThriftServerContext}
     * @param methodName          method name
     */
    default void doDone(ThriftServerContext thriftServerContext, String methodName) {}

    /**
     * 是否抛出异常
     * @return result
     */
    default boolean isThrowException() { return false; }


    default void doPreInvokeMethod(ThriftServerContext thriftServerContext, String methodName, Object[] args) {

    }
}
