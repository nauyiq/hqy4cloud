package com.hqy.cloud.rpc.registry.retry;

import com.hqy.foundation.timer.Timeout;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.registry.api.support.FailBackRegistry;

/**
 * Registry Fail Task.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/27 9:18
 */
public final class FailRegisteredTask extends AbstractRetryTask {

    private static final String NAME = "retry register task";

    public FailRegisteredTask(RPCModel rpcModel, FailBackRegistry registry) {
        super(rpcModel, registry, NAME);
    }

    @Override
    protected void doRetry(RPCModel rpcModel, FailBackRegistry registry, Timeout timeout) {
        registry.doRegister(rpcModel);
        registry.removeFailedRegisteredTask(rpcModel);
    }
}
