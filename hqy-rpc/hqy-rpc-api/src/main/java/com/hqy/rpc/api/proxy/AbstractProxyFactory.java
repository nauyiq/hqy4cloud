package com.hqy.rpc.api.proxy;

import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.api.ProxyFactory;
import com.hqy.rpc.api.InvocationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;

/**
 * AbstractProxyFactory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 11:40
 */
public abstract class AbstractProxyFactory implements ProxyFactory {

    private static final Logger log = LoggerFactory.getLogger(AbstractProxyFactory.class);

    @Override
    public <T> T getProxy(Invoker<T> invoker, InvocationCallback callback) throws RpcException {
        return getProxy(invoker, callback, false);
    }

    @Override
    public <T> T getProxy(Invoker<T> invoker, InvocationCallback callback, boolean generic) throws RpcException {
        // when compiling with native image, ensure that the order of the interfaces remains unchanged
        LinkedHashSet<Class<?>> interfaces = new LinkedHashSet<>();
        if (generic) {
            log.info("generic proxy service.");
            //TODO add generic extensional service
        }
        interfaces.add(invoker.getInterface());
        return getProxy(invoker, callback, interfaces.toArray(new Class<?>[0]));
    }

    /**
     * get dynamic proxy.
     * @param invoker   {@link Invoker}
     * @param callback  {@link InvocationCallback}
     * @param types     interfaces class.
     * @return          rpc service dynamic proxy.
     */
    public abstract <T> T getProxy(Invoker<T> invoker, InvocationCallback callback, Class<?>[] types);


}
