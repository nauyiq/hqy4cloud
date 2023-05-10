package com.hqy.cloud.rpc;

import com.hqy.cloud.rpc.fallback.Fallback;

import java.lang.reflect.Method;
import java.util.Map;

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
     */
    Class<?>[] getParameterTypes();

    /**
     * get arguments.
     * @return arguments.
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

    /**
     * add attachments.
     * @param attachment attachments
     */
    void addObjectAttachmentsIfAbsent(Map<String, Object> attachment);

    /**
     * get rpc obj attachments.
     * @return attachments
     */
    Map<String, Object> getObjectAttachments();

    /**
     * get this invocation fallback.
     * @param exType exception type
     * @return       {@link Fallback}
     */
    Fallback getFallback(Class<? extends Throwable> exType);

}
