package com.hqy.cloud.registry.api;

import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import com.hqy.cloud.registry.retry.*;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.thread.DefaultThreadFactory;
import com.hqy.cloud.util.timer.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.hqy.cloud.registry.common.Constants.DEFAULT_REGISTRY_RETRY_PERIOD;
import static com.hqy.cloud.registry.common.Constants.REGISTRY_RETRY_PERIOD_KEY;

/**
 * Failure automatically restores the registry
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 16:45
 */
public abstract class FailedBackRegistry extends AbstractRegistry {
    private static final Logger log = LoggerFactory.getLogger(FailedBackRegistry.class);

    private final Map<ProjectInfoModel, FailedRegisteredTask> failedRegistered = new ConcurrentHashMap<>();
    private final Map<ProjectInfoModel, FailedUnregisteredTask> failedUnregistered = new ConcurrentHashMap<>();
    private final Map<Holder, FailedSubscribedTask> failedSubscribed = new ConcurrentHashMap<>();
    private final Map<Holder, FailedUnsubscribedTask> failedUnsubscribed = new ConcurrentHashMap<>();
    private final int retryPeriod;
    private final HashedWheelTimer retryTimer;

    public FailedBackRegistry(ProjectInfoModel model) {
        super(model);
        this.retryPeriod = model.getParameter(REGISTRY_RETRY_PERIOD_KEY, DEFAULT_REGISTRY_RETRY_PERIOD);
        this.retryTimer = new HashedWheelTimer(new DefaultThreadFactory("FailedBackRegistryTimer"), retryPeriod, TimeUnit.MILLISECONDS, 128);
    }

    public void addFailedRegistered(ProjectInfoModel model) {
        FailedRegisteredTask failRegisteredTask = failedRegistered.get(model);
        if (failRegisteredTask != null) {
            return;
        }
        FailedRegisteredTask newTask = new FailedRegisteredTask(model, this);
        failRegisteredTask = failedRegistered.putIfAbsent(model, newTask);
        if (Objects.isNull(failRegisteredTask)) {
            // never has a retry task. then start a new task for retry.
            retryTimer.newTimeout(newTask, retryPeriod, TimeUnit.MILLISECONDS);
        }
    }

    public void addFailedUnregistered(ProjectInfoModel model) {
        FailedUnregisteredTask failedUnregisteredTask = failedUnregistered.get(model);
        if (failedUnregisteredTask != null) {
            return;
        }
        FailedUnregisteredTask newTask = new FailedUnregisteredTask(model, this);
        failedUnregisteredTask = failedUnregistered.putIfAbsent(model, newTask);
        if (Objects.isNull(failedUnregisteredTask)) {
            // never has a retry task. then start a new task for retry.
            retryTimer.newTimeout(newTask, retryPeriod, TimeUnit.MILLISECONDS);
        }
    }

    public void addFailedSubscribed(ProjectInfoModel model, ServiceNotifyListener listener) {
        Holder holder = new Holder(model, listener);
        FailedSubscribedTask failedSubscribedTask = failedSubscribed.get(holder);
        if (Objects.nonNull(failedSubscribedTask)) {
            return;
        }
        FailedSubscribedTask newTask = new FailedSubscribedTask(model, this, listener);
        failedSubscribedTask = failedSubscribed.putIfAbsent(holder, newTask);
        if (Objects.isNull(failedSubscribedTask)) {
            // never has a retry task. then start a new task for retry.
            retryTimer.newTimeout(newTask, retryPeriod, TimeUnit.MILLISECONDS);
        }
    }

    public void addFailedUnsubscribed(ProjectInfoModel model, ServiceNotifyListener listener) {
        Holder holder = new Holder(model, listener);
        FailedUnsubscribedTask failedUnsubscribedTask = failedUnsubscribed.get(holder);
        if (Objects.nonNull(failedUnsubscribedTask)) {
            return;
        }
        FailedUnsubscribedTask newTask = new FailedUnsubscribedTask(model, this, listener);
        failedUnsubscribedTask = failedUnsubscribed.putIfAbsent(holder, newTask);
        if (Objects.isNull(failedUnsubscribedTask)) {
            // never has a retry task. then start a new task for retry.
            retryTimer.newTimeout(newTask, retryPeriod, TimeUnit.MILLISECONDS);
        }
    }

    public void removeFailedRegistered(ProjectInfoModel model) {
        FailedRegisteredTask task = failedRegistered.remove(model);
        if (Objects.nonNull(task)) {
            task.cancel();
        }
    }

    public void removeFailedUnRegistered(ProjectInfoModel model) {
        FailedUnregisteredTask task = failedUnregistered.get(model);
        if (Objects.nonNull(task)) {
            task.cancel();
        }
    }

    public void removeFailSubscribed(ProjectInfoModel model, ServiceNotifyListener listener) {
        Holder holder = new Holder(model, listener);
        FailedSubscribedTask failedSubscribedTask = failedSubscribed.remove(holder);
        if (Objects.nonNull(failedSubscribedTask)) {
            failedSubscribedTask.cancel();
        }
        removeFailUnsubscribed(model, listener);
    }

    public void removeFailUnsubscribed(ProjectInfoModel model, ServiceNotifyListener listener) {
        Holder holder = new Holder(model, listener);
        FailedUnsubscribedTask failedUnsubscribedTask = failedUnsubscribed.get(holder);
        if (Objects.nonNull(failedUnsubscribedTask)) {
            failedUnsubscribedTask.cancel();
        }
    }


    public void removeFailedRegisteredTask(ProjectInfoModel model) {
        failedRegistered.remove(model);
    }

    public void removeFailedUnregisteredTask(ProjectInfoModel model) {
        failedUnregistered.remove(model);
    }

    public void removeFailedSubscribedTask(ProjectInfoModel model, ServiceNotifyListener listener) {
        Holder holder = new Holder(model, listener);
        failedSubscribed.remove(holder);
    }

    public void removeFailedUnsubscribedTask(ProjectInfoModel model, ServiceNotifyListener listener) {
        Holder holder = new Holder(model, listener);
        failedUnsubscribed.remove(holder);
    }

    public Map<ProjectInfoModel, FailedRegisteredTask> getFailedRegistered() {
        return failedRegistered;
    }

    public Map<ProjectInfoModel, FailedUnregisteredTask> getFailedUnregistered() {
        return failedUnregistered;
    }

    public Map<Holder, FailedSubscribedTask> getFailedSubscribed() {
        return failedSubscribed;
    }

    public Map<Holder, FailedUnsubscribedTask> getFailedUnsubscribed() {
        return failedUnsubscribed;
    }


    @Override
    public void register(ServiceInstance instance) {
        super.register(instance);
        ProjectInfoModel model = instance.getApplicationModel();
        removeFailedRegistered(model);
        removeFailedUnRegistered(model);

        try {
            // Sending a registration request to the server side
            doRegister(model);
        } catch (Throwable t) {
            // If the startup detection is opened, the Exception is thrown directly
            Throwable throwable = t;
            boolean check = CommonSwitcher.ENABLE_FAIL_BACK_REGISTRY_RETRY_CHECK.isOn() && model.getPort() != 0;
            boolean skipFailBack = throwable instanceof SkipFailBackWrapperException;
            if (check || skipFailBack) {
                if (skipFailBack) {
                    throwable = throwable.getCause();
                }
                throw new IllegalStateException("Failed to register " + model + " to registry " + getRegistryInfo() + ", cause: " + throwable.getMessage(), throwable);
            } else {
                log.error("Failed to register " + model + ", waiting for retry, cause: " + throwable.getMessage(), throwable);
            }
            // Record a failed registration request to a failed list, retry regularly
            addFailedRegistered(model);
        }
    }

    @Override
    public void unregister(ServiceInstance instance) {
        super.unregister(instance);
        ProjectInfoModel model = instance.getApplicationModel();
        removeFailedRegistered(model);
        removeFailedUnRegistered(model);

        try {
            // Sending a cancellation request to the server side
            doUnregister(model);
        } catch (Throwable t) {
            // If the startup detection is opened, the Exception is thrown directly
            Throwable throwable = t;
            boolean check = CommonSwitcher.ENABLE_FAIL_BACK_REGISTRY_RETRY_CHECK.isOn() && model.getPort() != 0;
            boolean skipFailBack = throwable instanceof SkipFailBackWrapperException;

            if (check || skipFailBack) {
                if (skipFailBack) {
                    throwable = t.getCause();
                }
                throw new IllegalStateException("Failed to unregister " + model + " to registry " + getRegistryInfo() + ", cause: " + throwable.getMessage(), throwable);
            } else {
                log.error("Failed to unregister " + model + ", waiting for retry, cause: " + throwable.getMessage(), throwable);
            }

            // Record a failed registration request to a failed list, retry regularly
            addFailedUnregistered(model);
        }
    }

    @Override
    public void subscribe(ServiceInstance instance, ServiceNotifyListener listener) {
        super.subscribe(instance, listener);
        ProjectInfoModel model = instance.getApplicationModel();
        removeFailSubscribed(model, listener);

        try {
            // Sending a subscription request to the server side
            doSubscribe(model, listener);
        } catch (Throwable t) {
            // If the startup detection is opened, the Exception is thrown directly
            Throwable throwable = t;
            boolean check = CommonSwitcher.ENABLE_FAIL_BACK_REGISTRY_RETRY_CHECK.isOn() && model.getPort() != 0;
            boolean skipFailBack = throwable instanceof SkipFailBackWrapperException;

            if (check || skipFailBack) {
                if (skipFailBack) {
                    throwable = t.getCause();
                }
                throw new IllegalStateException("Failed to subscribe " + model + ", cause: " + throwable.getMessage(), throwable);
            } else {
                log.error("Failed to subscribe " + model + ", waiting for retry, cause: " + throwable.getMessage(), throwable);
            }

            // Record a failed registration request to a failed list, retry regularly
            addFailedSubscribed(model, listener);
        }
    }

    @Override
    public void unsubscribe(ServiceInstance instance, ServiceNotifyListener listener) {
        super.unsubscribe(instance, listener);
        ProjectInfoModel model = instance.getApplicationModel();
        removeFailUnsubscribed(model, listener);

        try {
            // Sending a canceling subscription request to the server side
            doUnsubscribe(model, listener);
        } catch (Throwable t) {
            // If the startup detection is opened, the Exception is thrown directly
            Throwable throwable = t;
            boolean check = CommonSwitcher.ENABLE_FAIL_BACK_REGISTRY_RETRY_CHECK.isOn() && model.getPort() != 0;
            boolean skipFailBack = throwable instanceof SkipFailBackWrapperException;

            if (check || skipFailBack) {
                if (skipFailBack) {
                    throwable = t.getCause();
                }
                throw new IllegalStateException("Failed to unsubscribe " + model + " to registry " + getRegistryInfo().getDesc() + ", cause: " + throwable.getMessage(), throwable);
            } else {
                log.error("Failed to unsubscribe " + model + ", waiting for retry, cause: " + throwable.getMessage(), throwable);
            }

            // Record a failed registration request to a failed list, retry regularly
            addFailedUnsubscribed(model, listener);
        }

    }


    /**
     * retry register template method.
     * @param model registry information
     */
    public abstract void doRegister(ProjectInfoModel model);

    /**
     * retry unregister template method.
     * @param model unregister information
     */
    public abstract void doUnregister(ProjectInfoModel model);

    /**
     * retry subscribe template method.
     * @param model     subscribe information
     * @param listener notify listener
     */
    public abstract void doSubscribe(ProjectInfoModel model, ServiceNotifyListener listener);

    /**
     * retry unsubscribe template method.
     * @param model     unsubscribe information
     * @param listener  notify listener
     */
    public abstract void doUnsubscribe(ProjectInfoModel model, ServiceNotifyListener listener);

    static class Holder {
        private final ProjectInfoModel model;
        private final ServiceNotifyListener notifyListener;

        public Holder(ProjectInfoModel model, ServiceNotifyListener listener) {
            AssertUtil.isTrue(Objects.nonNull(model) && Objects.nonNull(listener), "url or listener is null.");
            this.model = model;
            this.notifyListener = listener;
        }

        @Override
        public int hashCode() {
            return model.hashCode() + notifyListener.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Holder h) {
                return this.model.equals(h.model) && this.notifyListener.equals(h.notifyListener);
            } else {
                return false;
            }
        }

    }
}
