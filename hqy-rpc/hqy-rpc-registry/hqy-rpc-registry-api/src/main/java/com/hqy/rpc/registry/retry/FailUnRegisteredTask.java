package com.hqy.rpc.registry.retry;

import com.hqy.foundation.timer.Timeout;
import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.registry.api.support.FailBackRegistry;

/**
 * UnRegistered Fail Task.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/27 11:26
 */
public final class FailUnRegisteredTask extends AbstractRetryTask {

    private static final String NAME = "retry unregistered task";

    public FailUnRegisteredTask(Metadata metadata, FailBackRegistry registry) {
        super(metadata, registry, NAME);
    }

    @Override
    protected void doRetry(Metadata metadata, FailBackRegistry registry, Timeout timeout) {
        registry.doUnregister(metadata);
        registry.removeFailedUnRegisteredTask(metadata);
    }

}
