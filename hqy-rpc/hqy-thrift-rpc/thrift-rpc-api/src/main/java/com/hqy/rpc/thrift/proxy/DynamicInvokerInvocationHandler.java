package com.hqy.rpc.thrift.proxy;

import com.hqy.rpc.api.Invoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 14:09
 */
public class DynamicInvokerInvocationHandler<T> implements InvocationHandler {

    private final Invoker<T> invoker;

    private static final String TOSTRING_METHOD_NAME = "toString";
    private static final String HASHCODE_METHOD_NAME = "hashCode";
    private static final String EQUALS_METHOD_NAME = "equals";
    private static final String DESTROY_METHOD_NAME = "$destroy";


    public DynamicInvokerInvocationHandler(Invoker<T> invoker) {
        this.invoker = invoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();

        if (parameterTypes.length == 0) {
            if (TOSTRING_METHOD_NAME.equals(methodName)) {
                return invoker.toString();
            } else if (DESTROY_METHOD_NAME.equals(methodName)) {
                invoker.destroy();
                return null;
            } else if (HASHCODE_METHOD_NAME.equals(methodName)) {
                return invoker.hashCode();
            }
        } else if (parameterTypes.length == 1 && EQUALS_METHOD_NAME.equals(methodName)) {
            return invoker.equals(args[0]);
        }




        return null;
    }
}
