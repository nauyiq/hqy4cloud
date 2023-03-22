package com.hqy.cloud.rpc.registry.retry;

import com.hqy.foundation.timer.Timeout;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.registry.api.support.FailBackRegistry;

/**
 * UnRegistered Fail Task.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/27 11:26
 */
public final class FailUnRegisteredTask extends AbstractRetryTask {

    private static final String NAME = "retry unregistered task";

    public FailUnRegisteredTask(RPCModel rpcModel, FailBackRegistry registry) {
        super(rpcModel, registry, NAME);
    }

    @Override
    protected void doRetry(RPCModel rpcModel, FailBackRegistry registry, Timeout timeout) {
        registry.doUnregister(rpcModel);
        registry.removeFailedUnRegisteredTask(rpcModel);
    }

}
