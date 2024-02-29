package com.hqy.cloud.registry.retry;

import com.hqy.cloud.registry.api.FailedBackRegistry;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.foundation.timer.Timeout;

/**
 * FailedRegisteredTask
 * @author qiyuan.hong
 * @version 1.0
 */
public final class FailedRegisteredTask extends AbstractRetryTask {
    private static final String NAME = "retry register task";

    public FailedRegisteredTask(ApplicationModel model, FailedBackRegistry registry) {
        super(model, registry, NAME);
    }

    @Override
    protected void doRetry(ApplicationModel model, FailedBackRegistry registry, Timeout timeout) {
        registry.doRegister(model);
        registry.removeFailedRegisteredTask(model);
    }
}
