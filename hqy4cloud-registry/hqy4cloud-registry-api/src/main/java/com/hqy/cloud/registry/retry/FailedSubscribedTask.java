package com.hqy.cloud.registry.retry;

import com.hqy.cloud.registry.api.FailedBackRegistry;
import com.hqy.cloud.registry.api.ServiceNotifyListener;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.timer.Timeout;

/**
 * FailedSubscribedTask.
 * @author qiyuan.hong
 * @version 1.0
 */
public final class FailedSubscribedTask extends AbstractRetryTask {
    private static final String NAME = "retry subscribed task";
    private final ServiceNotifyListener listener;

    public FailedSubscribedTask(ApplicationModel model, FailedBackRegistry registry, ServiceNotifyListener listener) {
        super(model, registry, NAME);
        AssertUtil.notNull(listener, "listener cat not be null.");
        this.listener = listener;
    }

    @Override
    protected void doRetry(ApplicationModel model, FailedBackRegistry registry, Timeout timeout) {
        registry.doSubscribe(model, listener);
        registry.removeFailedSubscribedTask(model, listener);
    }
}
