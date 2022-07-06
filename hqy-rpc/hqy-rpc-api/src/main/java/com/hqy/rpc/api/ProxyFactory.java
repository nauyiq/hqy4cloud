package com.hqy.rpc.api;

import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.rpc.common.Metadata;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 11:24
 */
public interface ProxyFactory {


    /**
     * create proxy.
     * @param invoker
     * @param <T>
     * @return
     * @throws RpcException
     */
    <T> T getProxy(Invoker<T> invoker) throws RpcException;

    /**
     * create proxy.
     * @param invoker
     * @param generic
     * @param <T>
     * @return
     * @throws RpcException
     */
    <T> T getProxy(Invoker<T> invoker, boolean generic) throws RpcException;

    /**
     * create invoker.
     * @param proxy
     * @param type
     * @param metadata
     * @param <T>
     * @return
     * @throws RpcException
     */
    <T> Invoker<T> getInvoker(T proxy, Class<T> type, Metadata metadata) throws RpcException;

}
