package com.hqy.rpc.registry.api.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.foundation.timer.HashedWheelTimer;
import com.hqy.rpc.registry.node.Metadata;
import com.hqy.rpc.registry.api.NotifyListener;
import com.hqy.rpc.registry.retry.FailRegisteredTask;
import com.hqy.rpc.registry.retry.FailSubscribedTask;
import com.hqy.rpc.registry.retry.FailUnRegisteredTask;
import com.hqy.rpc.registry.retry.FailUnsubscribedTask;
import com.hqy.util.AssertUtil;
import com.hqy.util.thread.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.hqy.rpc.registry.Constants.DEFAULT_REGISTRY_RETRY_PERIOD;
import static com.hqy.rpc.registry.Constants.REGISTRY_RETRY_PERIOD_KEY;

/**
 * Failure automatically restores the registry
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/24 17:26
 */
public abstract class FailBackRegistry extends AbstractRegistry {

    private static final Logger log = LoggerFactory.getLogger(FailBackRegistry.class);

    private final Map<Metadata, FailRegisteredTask> failRegistered = MapUtil.newConcurrentHashMap();

    private final Map<Metadata, FailUnRegisteredTask> failUnRegistered = MapUtil.newConcurrentHashMap();

    private final Map<Holder, FailSubscribedTask> failSubscribed = MapUtil.newConcurrentHashMap();

    private final Map<Holder, FailUnsubscribedTask> failUnsubscribed = MapUtil.newConcurrentHashMap();

    private final int retryPeriod;

    private final HashedWheelTimer retryTimer;

    public FailBackRegistry(Metadata metadata) {
        super(metadata);
        this.retryPeriod = metadata.getParameter(REGISTRY_RETRY_PERIOD_KEY, DEFAULT_REGISTRY_RETRY_PERIOD);
        retryTimer = new HashedWheelTimer(new DefaultThreadFactory("ThriftRegistryRetryTimer"), retryPeriod, TimeUnit.MINUTES, 128);
    }


    public void addFailedRegistered(Metadata metadata) {
        FailRegisteredTask failRegisteredTask = failRegistered.get(metadata);
        if (Objects.nonNull(failRegisteredTask)) {
            return;
        }
        FailRegisteredTask newTask = new FailRegisteredTask(metadata, this);
        failRegisteredTask = failRegistered.putIfAbsent(metadata, newTask);
        if (Objects.isNull(failRegisteredTask)) {
            // never has a retry task. then start a new task for retry.
            retryTimer.newTimeout(newTask, retryPeriod, TimeUnit.MILLISECONDS);
        }
    }

    public void removeFailedRegistered(Metadata metadata) {
        FailRegisteredTask task = failRegistered.remove(metadata);
        if (Objects.nonNull(task)) {
            task.cancel();
        }
    }

    public void removeFailedRegisteredTask(Metadata metadata) {
        failRegistered.remove(metadata);
    }


    public void addFailedUnregistered(Metadata metadata) {
        FailUnRegisteredTask failUnRegisteredTask = failUnRegistered.get(metadata);
        if (Objects.nonNull(failUnRegisteredTask)) {
            return;
        }
        FailUnRegisteredTask newTask = new FailUnRegisteredTask(metadata, this);
        failUnRegisteredTask = failUnRegistered.putIfAbsent(metadata, newTask);
        if (Objects.isNull(failUnRegisteredTask)) {
            // never has a retry task. then start a new task for retry.
            retryTimer.newTimeout(newTask, retryPeriod, TimeUnit.MILLISECONDS);
        }
    }

    public void removeFailedUnRegistered(Metadata metadata) {
        FailUnRegisteredTask task = failUnRegistered.get(metadata);
        if (Objects.nonNull(task)) {
            task.cancel();
        }
    }

    public void removeFailedUnRegisteredTask(Metadata metadata) {
        failUnRegistered.get(metadata);
    }



    public void addFailSubscribed(Metadata metadata, NotifyListener listener) {
        Holder holder = new Holder(metadata, listener);
        FailSubscribedTask failSubscribedTask = failSubscribed.get(holder);
        if (Objects.nonNull(failSubscribedTask)) {
            return;
        }
        FailSubscribedTask newTask = new FailSubscribedTask(metadata, this, listener);
        failSubscribedTask = failSubscribed.putIfAbsent(holder, newTask);
        if (Objects.isNull(failSubscribedTask)) {
            // never has a retry task. then start a new task for retry.
            retryTimer.newTimeout(newTask, retryPeriod, TimeUnit.MILLISECONDS);
        }
    }

    public void removeFailSubscribed(Metadata metadata, NotifyListener listener) {
        Holder holder = new Holder(metadata, listener);
        FailSubscribedTask failSubscribedTask = failSubscribed.remove(holder);
        if (Objects.nonNull(failSubscribedTask)) {
            failSubscribedTask.cancel();
        }
        removeFailUnsubscribed(metadata, listener);
    }

    public void removeFailSubscribedTask(Metadata metadata, NotifyListener listener) {
        Holder holder = new Holder(metadata, listener);
        failSubscribed.remove(holder);
    }


    public void addFailUnsubscribed(Metadata metadata, NotifyListener listener) {
        Holder holder = new Holder(metadata, listener);
        FailUnsubscribedTask failUnsubscribedTask = failUnsubscribed.get(holder);
        if (Objects.nonNull(failUnsubscribedTask)) {
            return;
        }
        FailUnsubscribedTask newTask = new FailUnsubscribedTask(metadata, this, listener);
        failUnsubscribedTask = failUnsubscribed.putIfAbsent(holder, newTask);
        if (Objects.isNull(failUnsubscribedTask)) {
            // never has a retry task. then start a new task for retry.
            retryTimer.newTimeout(newTask, retryPeriod, TimeUnit.MILLISECONDS);
        }
    }

    public void removeFailUnsubscribed(Metadata metadata, NotifyListener listener) {
        Holder holder = new Holder(metadata, listener);
        FailUnsubscribedTask failUnsubscribedTask = failUnsubscribed.get(holder);
        if (Objects.nonNull(failUnsubscribedTask)) {
            failUnsubscribedTask.cancel();
        }
    }

    public void removeFailUnsubscribedTask(Metadata metadata, NotifyListener listener) {
        Holder holder = new Holder(metadata, listener);
        failUnsubscribed.remove(holder);
    }


    public Map<Metadata, FailRegisteredTask> getFailRegistered() {
        return failRegistered;
    }

    public Map<Metadata, FailUnRegisteredTask> getFailUnRegistered() {
        return failUnRegistered;
    }

    public Map<Holder, FailSubscribedTask> getFailSubscribed() {
        return failSubscribed;
    }

    public Map<Holder, FailUnsubscribedTask> getFailUnsubscribed() {
        return failUnsubscribed;
    }

    @Override
    public void register(Metadata metadata) {
        super.register(metadata);
        removeFailedRegistered(metadata);
        removeFailedUnRegistered(metadata);

        try {
            // Sending a registration request to the server side
            doRegister(metadata);
        } catch (Throwable t) {
            // If the startup detection is opened, the Exception is thrown directly
            Throwable throwable = t;
            boolean check = CommonSwitcher.ENABLE_FAIL_BACK_REGISTRY_RETRY_CHECK.isOn() && metadata.getPort() != 0;
            boolean skipFailBack = throwable instanceof SkipFailBackWrapperException;
            if (check || skipFailBack) {
                if (skipFailBack) {
                    throwable = throwable.getCause();
                }
                throw new IllegalStateException("Failed to register " + metadata + " to registry " + getMetadata().getAddress() + ", cause: " + throwable.getMessage(), throwable);
            } else {
                log.error("Failed to register " + metadata + ", waiting for retry, cause: " + throwable.getMessage(), throwable);
            }

            // Record a failed registration request to a failed list, retry regularly
            addFailedRegistered(metadata);
        }
    }

    @Override
    public void reExportRegister(Metadata metadata) {
        super.register(metadata);
        removeFailedRegistered(metadata);
        removeFailedUnRegistered(metadata);

        try {
            // Sending a registration request to the server side
            doRegister(metadata);
        } catch (Throwable t) {
            if (!(t instanceof SkipFailBackWrapperException)) {
                throw new IllegalStateException("Failed to register (re-export) " + metadata + " to registry " + getMetadata().getAddress() + ", cause: " + t.getMessage(), t);
            }
        }
    }


    @Override
    public void unregister(Metadata metadata) {
        super.unregister(metadata);
        removeFailedRegistered(metadata);
        removeFailedUnRegistered(metadata);

        try {
            // Sending a cancellation request to the server side
            doUnregister(metadata);
        } catch (Throwable t) {
            // If the startup detection is opened, the Exception is thrown directly
            Throwable throwable = t;
            boolean check = CommonSwitcher.ENABLE_FAIL_BACK_REGISTRY_RETRY_CHECK.isOn() && metadata.getPort() != 0;
            boolean skipFailBack = throwable instanceof SkipFailBackWrapperException;

            if (check || skipFailBack) {
                if (skipFailBack) {
                    throwable = t.getCause();
                }
                throw new IllegalStateException("Failed to unregister " + metadata + " to registry " + getMetadata().getAddress() + ", cause: " + throwable.getMessage(), throwable);
            } else {
                log.error("Failed to unregister " + metadata + ", waiting for retry, cause: " + throwable.getMessage(), throwable);
            }

            // Record a failed registration request to a failed list, retry regularly
            addFailedUnregistered(metadata);
        }
    }


    @Override
    public void reExportUnregister(Metadata metadata) {
        super.unregister(metadata);
        removeFailedRegistered(metadata);
        removeFailedUnRegistered(metadata);

        try {
            // Sending a cancellation request to the server side
            doUnregister(metadata);
        } catch (Exception e) {
            if (!(e instanceof SkipFailBackWrapperException)) {
                throw new IllegalStateException("Failed to unregister(re-export) " + metadata + " to registry " + getMetadata().getAddress() + ", cause: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void subscribe(Metadata metadata, NotifyListener listener) {
        super.subscribe(metadata, listener);
        removeFailSubscribed(metadata, listener);

        try {
            // Sending a subscription request to the server side
            doSubscribe(metadata, listener);
        } catch (Throwable t) {
            // If the startup detection is opened, the Exception is thrown directly
            Throwable throwable = t;
            boolean check = CommonSwitcher.ENABLE_FAIL_BACK_REGISTRY_RETRY_CHECK.isOn() && metadata.getPort() != 0;
            boolean skipFailBack = throwable instanceof SkipFailBackWrapperException;

            if (check || skipFailBack) {
                if (skipFailBack) {
                    throwable = t.getCause();
                }
                throw new IllegalStateException("Failed to subscribe " + metadata + ", cause: " + throwable.getMessage(), throwable);
            } else {
                log.error("Failed to subscribe " + metadata + ", waiting for retry, cause: " + throwable.getMessage(), throwable);
            }

            // Record a failed registration request to a failed list, retry regularly
            addFailSubscribed(metadata, listener);
        }
    }

    @Override
    public void unsubscribe(Metadata metadata, NotifyListener listener) {
        super.unsubscribe(metadata, listener);
        removeFailUnsubscribed(metadata, listener);

        try {
            // Sending a canceling subscription request to the server side
            doUnsubscribe(metadata, listener);
        } catch (Throwable t) {
            // If the startup detection is opened, the Exception is thrown directly
            Throwable throwable = t;
            boolean check = CommonSwitcher.ENABLE_FAIL_BACK_REGISTRY_RETRY_CHECK.isOn() && metadata.getPort() != 0;
            boolean skipFailBack = throwable instanceof SkipFailBackWrapperException;

            if (check || skipFailBack) {
                if (skipFailBack) {
                    throwable = t.getCause();
                }
                throw new IllegalStateException("Failed to unsubscribe " + metadata + " to registry " + getMetadata().getAddress() + ", cause: " + throwable.getMessage(), throwable);
            } else {
                log.error("Failed to unsubscribe " + metadata + ", waiting for retry, cause: " + throwable.getMessage(), throwable);
            }

            // Record a failed registration request to a failed list, retry regularly
            addFailUnsubscribed(metadata, listener);
        }
    }


    @Override
    protected void notify(Metadata metadata, NotifyListener listener, List<Metadata> metadataList) {
        AssertUtil.notNull(metadata, "FailBackRegistry notify url is null.");
        AssertUtil.notNull(listener, "FailBackRegistry notify listener is null.");

        try {
            doNotify(metadata, listener, metadataList);
        } catch (Throwable t) {
            log.error("Failed to notify addresses for subscribe " + metadata + ", cause: " + t.getMessage(), t);
        }
    }

    protected void doNotify(Metadata metadata, NotifyListener listener, List<Metadata> metadataList) {
        super.notify(metadata, listener, metadataList);
    }

    /**
     * retry register template method.
     * @param metadata registry information
     */
    public abstract void doRegister(Metadata metadata);

    /**
     * retry unRegistry template method.
     * @param metadata unRegistry information
     */
    public abstract void doUnregister(Metadata metadata);

    /**
     * retry subscribe template method.
     * @param metadata      subscribe information
     * @param listener notify listener
     */
    public abstract void doSubscribe(Metadata metadata, NotifyListener listener);

    /**
     * retry unsubscribe template method.
     * @param metadata       unsubscribe information
     * @param listener  notify listener
     */
    public abstract void doUnsubscribe(Metadata metadata, NotifyListener listener);


    static class Holder {
        private final Metadata metadata;

        private final NotifyListener notifyListener;

        public Holder(Metadata metadata, NotifyListener listener) {
            AssertUtil.isTrue(Objects.nonNull(metadata) && Objects.nonNull(listener), "url or listener is null.");
            this.metadata = metadata;
            this.notifyListener = listener;
        }

        @Override
        public int hashCode() {
            return metadata.hashCode() + notifyListener.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Holder) {
                Holder h = (Holder) obj;
                return this.metadata.equals(h.metadata) && this.notifyListener.equals(h.notifyListener);
            } else {
                return false;
            }
        }

    }
}
