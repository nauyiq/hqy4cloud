package com.hqy.rpc.monitor;

import java.io.Serializable;

/**
 * rpc collect information.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/20 16:32
 */
public class CollectionData implements Serializable {
    private static final long serialVersionUID = -8097290602617530819L;

    private final String caller;

    private final String provider;

    private final Class<?> serviceClass;

    private final String method;

    private final long startMillis;

    private final long costMillis;

    private final boolean rpcResult;

    private final Throwable exception;

    public CollectionData(String caller, String provider, Class<?> serviceClass, String method, long startMillis, long costMillis, boolean rpcResult) {
       this(caller, provider, serviceClass, method, startMillis, costMillis, rpcResult, null);
    }

    public CollectionData(String caller, String provider, Class<?> serviceClass, String method, long startMillis, long costMillis, boolean rpcResult, Throwable exception) {
        this.caller = caller;
        this.provider = provider;
        this.serviceClass = serviceClass;
        this.method = method;
        this.startMillis = startMillis;
        this.costMillis = costMillis;
        this.rpcResult = rpcResult;
        this.exception = exception;
    }

    public String getCaller() {
        return caller;
    }

    public String getProvider() {
        return provider;
    }

    public Class<?> getServiceClass() {
        return serviceClass;
    }

    public String getMethod() {
        return method;
    }

    public long getCostMillis() {
        return costMillis;
    }

    public boolean isRpcResult() {
        return rpcResult;
    }

    public Throwable getException() {
        return exception;
    }

    public long getStartMillis() {
        return startMillis;
    }
}
