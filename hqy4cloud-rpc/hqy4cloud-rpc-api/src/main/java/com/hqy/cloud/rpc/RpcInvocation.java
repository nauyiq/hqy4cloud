package com.hqy.cloud.rpc;

import com.hqy.cloud.rpc.fallback.Fallback;
import com.hqy.cloud.rpc.fallback.GlobalFallbackContext;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * RPC Invocation.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29
 */
public class RpcInvocation implements Invocation, Serializable {

    @Serial
    private static final long serialVersionUID = -1549902387737186173L;

    private final transient Invoker<?> invoker;

    private final transient Class<?> returnType;

    private final transient Class<?>[] returnTypes;

    private final String methodName;

    private final String serviceName;

    private final transient Class<?>[] parameterTypes;

    private final Object[] arguments;

    private Map<String, Object> attachments;

    private final transient Lock attachmentLock = new ReentrantLock();

    private transient Method method;

    private transient InvokeMode invokeMode;

    private final InvocationCallback invocationCallback;



    public RpcInvocation(Invoker<?> invoker, Method method, Object[] args) {
        this(invoker, null, method, args, InvokeMode.SYNC, new HashMap<>());
    }

    public RpcInvocation(Invoker<?> invoker, InvocationCallback invocationCallback, Method method, Object[] args) {
        this(invoker, invocationCallback, method, args, InvokeMode.SYNC, new HashMap<>());
    }

    public RpcInvocation(Invoker<?> invoker, InvocationCallback invocationCallback, Method method, Object[] args, InvokeMode invokeMode, Map<String, Object> attachments) {
        this.invoker = invoker;
        this.invocationCallback = invocationCallback;
        this.method = method;
        this.arguments = args;
        this.attachments = attachments;
        this.serviceName = invoker.getInterface().getSimpleName();
        this.returnType = method.getReturnType();
        this.returnTypes = method.getParameterTypes();
        this.parameterTypes = method.getParameterTypes();
        this.methodName = method.getName();
        this.invokeMode = invokeMode;
    }


    public Type getReturnType() {
        return returnType;
    }

    public Type[] getReturnTypes() {
        return returnTypes;
    }

    public void setAttachments(Map<String, Object> attachments) {
        this.attachments = attachments;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setInvokeMode(InvokeMode invokeMode) {
        this.invokeMode = invokeMode;
    }

    @Override
    public InvocationCallback getInvocationCallback() {
        return invocationCallback;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Invoker<?> getInvoker() {
        return invoker;
    }

    @Override
    public InvokeMode getInvokeMode() {
        return invokeMode;
    }

    @Override
    public void addObjectAttachmentsIfAbsent(Map<String, Object> attachments) {
        if (attachments == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : attachments.entrySet()) {
            setAttachmentIfAbsent(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Map<String, Object> getObjectAttachments() {
        return attachments;
    }

    @Override
    public Fallback getFallback(Class<? extends Throwable> exType) {
        return GlobalFallbackContext.getFallback(exType);
    }

    public void setAttachmentIfAbsent(String key, Object value) {
        setObjectAttachmentIfAbsent(key, value);
    }

    public void setObjectAttachmentIfAbsent(String key, Object value) {
        attachmentLock.lock();
        try {
            if (attachments == null) {
                attachments = new HashMap<>(4);
            }
            if (!attachments.containsKey(key)) {
                attachments.put(key, value);
            }
        } finally {
            attachmentLock.unlock();
        }
    }


}
