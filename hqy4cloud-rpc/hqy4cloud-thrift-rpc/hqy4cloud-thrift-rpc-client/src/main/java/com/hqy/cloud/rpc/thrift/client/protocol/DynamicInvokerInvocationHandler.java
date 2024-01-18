package com.hqy.cloud.rpc.thrift.client.protocol;

import com.hqy.cloud.rpc.InvocationCallback;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Dynamic proxy obj.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29
 */
public class DynamicInvokerInvocationHandler<T> implements InvocationHandler {
    private final Invoker<T> invoker;
    private final InvocationCallback invocationCallback;

    private static final String TOSTRING_METHOD_NAME = "toString";
    private static final String HASHCODE_METHOD_NAME = "hashCode";
    private static final String EQUALS_METHOD_NAME = "equals";
    private static final String DESTROY_METHOD_NAME = "destroy";

    public DynamicInvokerInvocationHandler(Invoker<T> invoker, InvocationCallback invocationCallback) {
        this.invoker = invoker;
        this.invocationCallback = invocationCallback;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();

        if (parameterTypes.length == 0) {
            switch (methodName) {
                case TOSTRING_METHOD_NAME:
                    return invoker.toString();
                case DESTROY_METHOD_NAME:
                    invoker.destroy();
                    return null;
                case HASHCODE_METHOD_NAME:
                    return invoker.hashCode();
                default:
                    break;
            }
        } else if (parameterTypes.length == 1 && EQUALS_METHOD_NAME.equals(methodName)) {
            return invoker.equals(args[0]);
        }


        RpcInvocation rpcInvocation = new RpcInvocation(invoker, invocationCallback, method, args);
        return invoker.invoke(rpcInvocation);
    }


}
