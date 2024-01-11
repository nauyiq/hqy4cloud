package com.hqy.cloud.rpc;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.model.RpcModel;

/**
 * {@link Invoker} proxy factory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29
 */
public interface ProxyFactory {

    /**
     * create proxy.
     * @param invoker   rpc interface invoker.
     * @param callback  invocation callback.
     * @param <T>       interface class type.
     * @return          proxy for rpc interface.
     * @throws RpcException exception.
     */
    <T> T getProxy(Invoker<T> invoker, InvocationCallback callback) throws RpcException;

    /**
     * create proxy.
     * @param invoker    rpc interface invoker.
     * @param callback   invocation callback.
     * @param generic    user generic service?
     * @param <T>        interface class type
     * @return           proxy for rpc interface.
     * @throws RpcException exception.
     */
    <T> T getProxy(Invoker<T> invoker, InvocationCallback callback, boolean generic) throws RpcException;

    /**
     * from proxy get Invoker
     * @param proxy         proxy for rpc interface.
     * @param type          interface class type
     * @param rpcModel    rpc context
     * @return              {@link Invoker}
     * @throws RpcException exception.
     */
    <T> Invoker<T> getInvoker(T proxy, Class<T> type, RpcModel rpcModel) throws RpcException;

}
