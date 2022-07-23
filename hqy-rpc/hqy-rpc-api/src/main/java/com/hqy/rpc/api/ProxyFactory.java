package com.hqy.rpc.api;

import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.rpc.common.support.RPCModel;

/**
 * {@link Invoker} proxy factory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 11:24
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
    <T> Invoker<T> getInvoker(T proxy, Class<T> type, RPCModel rpcModel) throws RpcException;

}
