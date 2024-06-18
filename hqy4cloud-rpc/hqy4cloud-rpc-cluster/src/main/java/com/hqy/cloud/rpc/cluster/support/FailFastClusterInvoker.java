package com.hqy.cloud.rpc.cluster.support;

import cn.hutool.core.net.NetUtil;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.Invocation;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.cluster.directory.Directory;
import com.hqy.cloud.rpc.cluster.loadbalance.LoadBalance;

import java.util.List;
import java.util.Map;

/**
 * Execute exactly once, which means this policy will throw an exception immediately in case of an invocation error.
 * Usually used for non-idempotent write operations
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13
 */
public class FailFastClusterInvoker<T> extends AbstractClusterInvoker<T> {

    public FailFastClusterInvoker(Directory<T> directory, Map<String, Object> attachments) {
        super(directory, attachments);
    }

    @Override
    protected Object doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadBalance) throws RpcException {
        checkInvokers(invokers, invocation);
        Invoker<T> invoker = select(loadBalance, invocation, invokers, null);
        try {
            return invoker.invoke(invocation);
        } catch (Throwable e) {
            // biz exception.
            if (e instanceof RpcException && ((RpcException) e).isBiz()) {
                throw (RpcException) e;
            }
            throw new RpcException(e instanceof RpcException ? ((RpcException) e).getCode() : 0,
                    "FailFast invoke providers " + invoker.getModel() + " " + loadBalance.getClass().getSimpleName()
                            + " for service " + getInterface().getName()
                            + " method " + invocation.getMethodName() + " on consumer " + NetUtil.getLocalhostStr()
                            + ", but no luck to perform the invocation. Last error is: " + e.getMessage(),
                    e.getCause() != null ? e.getCause() : e);
        }
    }
}
