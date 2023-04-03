package com.hqy.cloud.rpc;

import java.lang.reflect.Method;

/**
 * Invocation.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 11:20
 */
public interface Invocation {

    /**
     * get method name.
     * @return method name
     */
    String getMethodName();

    /**
     * get the rpc service interface name.
     * @return  service interface name
     */
    String getServiceName();

    /**
     * return rpc method
     * @return {@link Method}
     */
    Method getMethod();

    /**
     * get the invoker in current context.
     * @return invoker.
     */
    Invoker<?> getInvoker();


    /**
     * get the rpc service method parameter types.
     * @return parameter types.
     * @serial
     */
    Class<?>[] getParameterTypes();

    /**
     * get arguments.
     * @return arguments.
     * @serial
     */
    Object[] getArguments();

    /**
     * get invoker mode
     * @return {@link InvokeMode}
     */
    InvokeMode getInvokeMode();

    /**
     * get invocationCallback.
     * @return {@link InvocationCallback}
     */
    InvocationCallback getInvocationCallback();


}
