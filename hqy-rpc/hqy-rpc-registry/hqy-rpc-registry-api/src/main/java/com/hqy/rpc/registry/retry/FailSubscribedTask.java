package com.hqy.rpc.registry.retry;

import com.hqy.foundation.timer.Timeout;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.registry.api.NotifyListener;
import com.hqy.rpc.registry.api.support.FailBackRegistry;
import com.hqy.util.AssertUtil;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/27 11:52
 */
public final class FailSubscribedTask extends AbstractRetryTask {

    private static final String NAME = "retry subscribed task";

    private final NotifyListener listener;

    public FailSubscribedTask(RPCModel rpcModel, FailBackRegistry registry, NotifyListener listener) {
        super(rpcModel, registry, NAME);
        AssertUtil.notNull(listener, "listener cat not be null.");
        this.listener = listener;
    }

    @Override
    protected void doRetry(RPCModel rpcModel, FailBackRegistry registry, Timeout timeout) {
        registry.doSubscribe(rpcModel, listener);
        registry.removeFailSubscribedTask(rpcModel, listener);
    }
}
