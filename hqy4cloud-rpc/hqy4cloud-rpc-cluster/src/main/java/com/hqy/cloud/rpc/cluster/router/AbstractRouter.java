package com.hqy.cloud.rpc.cluster.router;

import com.hqy.cloud.rpc.model.RpcModel;

/**
 * abstract Router.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/30 17:28
 */
public abstract class AbstractRouter<T> implements Router<T> {
    protected static final transient String FORCE_KEY = "force";

    protected RpcModel rpcModel;
    protected int priority;

    @Override
    public RpcModel getRpcModel() {
        return rpcModel;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(Router<T> o) {
        return Integer.compare(this.getPriority(), o.getPriority());
    }
}
