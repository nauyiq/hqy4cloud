package com.hqy.rpc;

import java.io.Serializable;

/**
 * RPC Invocation.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 14:56
 */
public class RpcInvocation implements Invocation, Serializable {
    private static final long serialVersionUID = -1549902387737186173L;

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return new Class[0];
    }

    @Override
    public Object[] getArguments() {
        return new Object[0];
    }

    @Override
    public InvokerCallback getInvokerCallback() {
        return null;
    }
}
