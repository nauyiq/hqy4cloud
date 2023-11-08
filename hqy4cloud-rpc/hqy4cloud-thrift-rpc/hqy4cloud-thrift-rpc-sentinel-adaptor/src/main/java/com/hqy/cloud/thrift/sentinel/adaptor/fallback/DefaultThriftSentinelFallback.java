package com.hqy.cloud.thrift.sentinel.adaptor.fallback;

import com.hqy.cloud.rpc.Invocation;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.fallback.Fallback;
import com.hqy.cloud.thrift.sentinel.adaptor.exception.ThriftSentinelBlockException;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/6 9:47
 */
public class DefaultThriftSentinelFallback implements Fallback {

    @Override
    public Class<? extends Exception> exceptionType() {
        return ThriftSentinelBlockException.class;
    }

    @Override
    public Object handle(Invoker<?> invoker, Invocation invocation, Exception ex) throws Exception {
        throw ex;
    }
}
