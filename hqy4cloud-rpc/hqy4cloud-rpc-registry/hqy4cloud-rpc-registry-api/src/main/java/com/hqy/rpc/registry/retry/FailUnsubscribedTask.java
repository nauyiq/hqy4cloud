package com.hqy.rpc.registry.retry;

import com.hqy.foundation.timer.Timeout;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.registry.api.NotifyListener;
import com.hqy.rpc.registry.api.support.FailBackRegistry;
import com.hqy.util.AssertUtil;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/27 13:42
 */
public final class FailUnsubscribedTask extends AbstractRetryTask {

    private static final String NAME = "retry unsubscribe task";

    private final NotifyListener listener;

    public FailUnsubscribedTask(RPCModel rpcModel, FailBackRegistry registry, NotifyListener listener) {
        super(rpcModel, registry, NAME);
        AssertUtil.notNull(listener, "listener cat not be null.");
        this.listener = listener;
    }

    @Override
    protected void doRetry(RPCModel rpcModel, FailBackRegistry registry, Timeout timeout) {
        registry.doUnsubscribe(rpcModel, listener);
        registry.removeFailUnsubscribedTask(rpcModel, listener);
    }
}
