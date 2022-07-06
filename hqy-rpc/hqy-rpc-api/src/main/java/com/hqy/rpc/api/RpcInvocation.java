package com.hqy.rpc.api;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * RPC Invocation.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 14:56
 */
public class RpcInvocation implements Invocation, Serializable {
    private static final long serialVersionUID = -1549902387737186173L;

    private transient Invoker<?> invoker;

    private transient Class<?> returnType;

    private transient Type[] returnTypes;

    private String methodName;

    private String interfaceName;

    private transient Class<?>[] parameterTypes;

    private Object[] arguments;

    private Map<String, Object> attachments;

    private final transient Lock attachmentLock = new ReentrantLock();


    private final transient InvokerCallback invokerCallback;

    private final transient String hashFactor;

    public RpcInvocation(InvokerCallback invokerCallback, String hashFactor) {
        this.invokerCallback = invokerCallback;
        this.hashFactor = hashFactor;
    }

    public static Invocation createInvocation(InvokerCallback invokerCallback, String hashFactor) {
        return new RpcInvocation(invokerCallback, hashFactor);
    }


    public void setInvoker(Invoker<?> invoker) {
        this.invoker = invoker;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public void setReturnTypes(Type[] returnTypes) {
        this.returnTypes = returnTypes;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public void setAttachments(Map<String, Object> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getServiceName() {
        return interfaceName;
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
    public InvokerCallback getInvokerCallback() {
        return invokerCallback;
    }

    @Override
    public String getHashFactor() {
        return hashFactor;
    }

    @Override
    public Invoker<?> getInvoker() {
        return invoker;
    }

    public void addObjectAttachmentsIfAbsent(Map<String, Object> attachments) {
        if (attachments == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : attachments.entrySet()) {
            setAttachmentIfAbsent(entry.getKey(), entry.getValue());
        }
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
