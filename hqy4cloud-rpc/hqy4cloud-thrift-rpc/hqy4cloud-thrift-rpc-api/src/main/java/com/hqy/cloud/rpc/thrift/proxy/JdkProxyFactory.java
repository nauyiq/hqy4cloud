package com.hqy.cloud.rpc.thrift.proxy;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.InvocationCallback;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.proxy.AbstractProxyFactory;
import com.hqy.cloud.rpc.model.RPCModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 14:02
 */
public class JdkProxyFactory extends AbstractProxyFactory {

    private static final Logger log = LoggerFactory.getLogger(JdkProxyFactory.class);

    @SuppressWarnings("rawtypes")
    private static final Cache<Invoker, DynamicInvokerInvocationHandler> INVOCATION_HANDLER_CACHE =
            CacheBuilder.newBuilder().initialCapacity(512).expireAfterWrite(1L, TimeUnit.HOURS).build();

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, RPCModel rpcModel) throws RpcException {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, InvocationCallback callback, Class<?>[] interfaces) {
        if (callback != null) {
            log.info("InvocationCallback not null, create new DynamicInvokerInvocationHandler.");
            return (T) Proxy.newProxyInstance(invoker.getInterface().getClassLoader(), interfaces, new DynamicInvokerInvocationHandler<>(invoker, callback));
        }

        DynamicInvokerInvocationHandler<T> handler = INVOCATION_HANDLER_CACHE.getIfPresent(invoker);
        if (handler == null) {
            handler = new DynamicInvokerInvocationHandler<>(invoker, null);
            INVOCATION_HANDLER_CACHE.put(invoker, handler);
        }

        return (T) Proxy.newProxyInstance(invoker.getInterface().getClassLoader(), interfaces, handler);
    }
}
