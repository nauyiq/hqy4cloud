package com.hqy.cloud.rpc.registry.retry;

import com.hqy.foundation.timer.Timeout;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.registry.api.NotifyListener;
import com.hqy.cloud.rpc.registry.api.support.FailBackRPCRegistry;
import com.hqy.cloud.util.AssertUtil;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/27 13:42
 */
public final class FailUnsubscribedTask extends AbstractRetryTask {

    private static final String NAME = "retry unsubscribe task";

    private final NotifyListener listener;

    public FailUnsubscribedTask(RPCModel rpcModel, FailBackRPCRegistry registry, NotifyListener listener) {
        super(rpcModel, registry, NAME);
        AssertUtil.notNull(listener, "listener cat not be null.");
        this.listener = listener;
    }

    @Override
    protected void doRetry(RPCModel rpcModel, FailBackRPCRegistry registry, Timeout timeout) {
        registry.doUnsubscribe(rpcModel, listener);
        registry.removeFailUnsubscribedTask(rpcModel, listener);
    }
}
