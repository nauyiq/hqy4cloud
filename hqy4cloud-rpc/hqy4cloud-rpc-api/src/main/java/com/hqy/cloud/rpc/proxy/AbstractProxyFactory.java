package com.hqy.cloud.rpc.proxy;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.InvocationCallback;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.ProxyFactory;
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
