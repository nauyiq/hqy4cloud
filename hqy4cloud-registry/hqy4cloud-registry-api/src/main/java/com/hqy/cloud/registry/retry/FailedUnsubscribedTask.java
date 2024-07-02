package com.hqy.cloud.registry.retry;

import com.hqy.cloud.registry.api.FailedBackRegistry;
import com.hqy.cloud.registry.api.ServiceNotifyListener;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.util.timer.Timeout;

/**
 * FailedUnsubscribedTask.
 * @author qiyuan.hong
 * @version 1.0
 */
public final class FailedUnsubscribedTask extends AbstractRetryTask {
    private static final String NAME = "retry unsubscribe task";
    private final ServiceNotifyListener listener;

    public FailedUnsubscribedTask(ApplicationModel model, FailedBackRegistry registry, ServiceNotifyListener listener) {
        super(model, registry, NAME);
        this.listener = listener;
    }

    @Override
    protected void doRetry(ApplicationModel model, FailedBackRegistry registry, Timeout timeout) {
        registry.doUnsubscribe(model, listener);
        registry.removeFailedUnsubscribedTask(model, listener);
    }
}
