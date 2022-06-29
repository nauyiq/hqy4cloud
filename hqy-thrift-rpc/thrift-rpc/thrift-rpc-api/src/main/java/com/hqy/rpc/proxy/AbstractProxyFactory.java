package com.hqy.rpc.proxy;

import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.rpc.Invoker;
import com.hqy.rpc.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 11:40
 */
public abstract class AbstractProxyFactory implements ProxyFactory {

    private static final Logger log = LoggerFactory.getLogger(AbstractProxyFactory.class);



    @Override
    public <T> T getProxy(Invoker<T> invoker) throws RpcException {
        return getProxy(invoker, false);
    }

    @Override
    public <T> T getProxy(Invoker<T> invoker, boolean generic) throws RpcException {
        // when compiling with native image, ensure that the order of the interfaces remains unchanged
        LinkedHashSet<Class<?>> interfaces = new LinkedHashSet<>();

        if (generic) {
            //TODO add generic extensional service
        }

        interfaces.add(invoker.getInterface());
        try {
            return getProxy(invoker, interfaces.toArray(new Class<?>[0]));
        } catch (Throwable t) {
            throw t;
        }
    }

    public abstract <T> T getProxy(Invoker<T> invoker, Class<?>[] types);


}
