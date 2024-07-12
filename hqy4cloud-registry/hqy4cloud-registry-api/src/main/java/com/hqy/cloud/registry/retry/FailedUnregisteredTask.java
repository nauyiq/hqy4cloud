package com.hqy.cloud.registry.retry;

import com.hqy.cloud.registry.api.FailedBackRegistry;
import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import com.hqy.cloud.util.timer.Timeout;

/**
 * UnRegistered Fail Task.
 * @author qiyuan.hong
 * @version 1.0
 */
public final class FailedUnregisteredTask extends AbstractRetryTask {
    private static final String NAME = "retry unregistered task";

    public FailedUnregisteredTask(ProjectInfoModel model, FailedBackRegistry registry) {
        super(model, registry, NAME);
    }

    @Override
    protected void doRetry(ProjectInfoModel model, FailedBackRegistry registry, Timeout timeout) {
        registry.doUnregister(model);
        registry.removeFailedUnregisteredTask(model);
    }
}
