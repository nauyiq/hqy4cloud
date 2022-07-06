package com.hqy.rpc.thrift.proxy;

import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.api.proxy.AbstractProxyFactory;
import com.hqy.rpc.common.Metadata;

import java.lang.reflect.Proxy;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 14:02
 */
public class JdkProxyFactory extends AbstractProxyFactory {

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, Metadata metadata) throws RpcException {
        return null;
        //        return (T)  ;
    }


    @SuppressWarnings("unchecked")
    @Override
    public  <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
        return (T) Proxy.newProxyInstance(invoker.getInterface().getClassLoader(), interfaces, new DynamicInvokerInvocationHandler<>(invoker));
    }
}
