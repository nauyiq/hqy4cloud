package com.hqy.cloud.rpc.registry.api.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.foundation.timer.HashedWheelTimer;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.registry.api.NotifyListener;
import com.hqy.cloud.rpc.registry.retry.FailRegisteredTask;
import com.hqy.cloud.rpc.registry.retry.FailSubscribedTask;
import com.hqy.cloud.rpc.registry.retry.FailUnRegisteredTask;
import com.hqy.cloud.rpc.registry.retry.FailUnsubscribedTask;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.thread.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.hqy.cloud.rpc.registry.Constants.DEFAULT_REGISTRY_RETRY_PERIOD;
import static com.hqy.cloud.rpc.registry.Constants.REGISTRY_RETRY_PERIOD_KEY;

/**
 * Failure automatically restores the registry
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/24 17:26
 */
public abstract class FailBackRegistry extends AbstractRegistry {

    private static final Logger log = LoggerFactory.getLogger(FailBackRegistry.class);

    private final Map<RPCModel, FailRegisteredTask> failRegistered = MapUtil.newConcurrentHashMap();

    private final Map<RPCModel, FailUnRegisteredTask> failUnRegistered = MapUtil.newConcurrentHashMap();

    private final Map<Holder, FailSubscribedTask> failSubscribed = MapUtil.newConcurrentHashMap();

    private final Map<Holder, FailUnsubscribedTask> failUnsubscribed = MapUtil.newConcurrentHashMap();

    private final int retryPeriod;

    private final HashedWheelTimer retryTimer;

    public FailBackRegistry(RPCModel rpcModel) {
        super(rpcModel);
        this.retryPeriod = rpcModel.getParameter(REGISTRY_RETRY_PERIOD_KEY, DEFAULT_REGISTRY_RETRY_PERIOD);
        retryTimer = new HashedWheelTimer(new DefaultThreadFactory("ThriftRegistryRetryTimer"), retryPeriod, TimeUnit.MINUTES, 128);
    }


    public void addFailedRegistered(RPCModel rpcModel) {
        FailRegisteredTask failRegisteredTask = failRegistered.get(rpcModel);
        if (Objects.nonNull(failRegisteredTask)) {
            return;
        }
        FailRegisteredTask newTask = new FailRegisteredTask(rpcModel, this);
        failRegisteredTask = failRegistered.putIfAbsent(rpcModel, newTask);
        if (Objects.isNull(failRegisteredTask)) {
            // never has a retry task. then start a new task for retry.
            retryTimer.newTimeout(newTask, retryPeriod, TimeUnit.MILLISECONDS);
        }
    }

    public void removeFailedRegistered(RPCModel rpcModel) {
        FailRegisteredTask task = failRegistered.remove(rpcModel);
        if (Objects.nonNull(task)) {
            task.cancel();
        }
    }

    public void removeFailedRegisteredTask(RPCModel rpcModel) {
        failRegistered.remove(rpcModel);
    }


    public void addFailedUnregistered(RPCModel rpcModel) {
        FailUnRegisteredTask failUnRegisteredTask = failUnRegistered.get(rpcModel);
        if (Objects.nonNull(failUnRegisteredTask)) {
            return;
        }
        FailUnRegisteredTask newTask = new FailUnRegisteredTask(rpcModel, this);
        failUnRegisteredTask = failUnRegistered.putIfAbsent(rpcModel, newTask);
        if (Objects.isNull(failUnRegisteredTask)) {
            // never has a retry task. then start a new task for retry.
            retryTimer.newTimeout(newTask, retryPeriod, TimeUnit.MILLISECONDS);
        }
    }

    public void removeFailedUnRegistered(RPCModel rpcModel) {
        FailUnRegisteredTask task = failUnRegistered.get(rpcModel);
        if (Objects.nonNull(task)) {
            task.cancel();
        }
    }

    public void removeFailedUnRegisteredTask(RPCModel rpcModel) {
        failUnRegistered.get(rpcModel);
    }



    public void addFailSubscribed(RPCModel rpcModel, NotifyListener listener) {
        Holder holder = new Holder(rpcModel, listener);
        FailSubscribedTask failSubscribedTask = failSubscribed.get(holder);
        if (Objects.nonNull(failSubscribedTask)) {
            return;
        }
        FailSubscribedTask newTask = new FailSubscribedTask(rpcModel, this, listener);
        failSubscribedTask = failSubscribed.putIfAbsent(holder, newTask);
        if (Objects.isNull(failSubscribedTask)) {
            // never has a retry task. then start a new task for retry.
            retryTimer.newTimeout(newTask, retryPeriod, TimeUnit.MILLISECONDS);
        }
    }

    public void removeFailSubscribed(RPCModel rpcModel, NotifyListener listener) {
        Holder holder = new Holder(rpcModel, listener);
        FailSubscribedTask failSubscribedTask = failSubscribed.remove(holder);
        if (Objects.nonNull(failSubscribedTask)) {
            failSubscribedTask.cancel();
        }
        removeFailUnsubscribed(rpcModel, listener);
    }

    public void removeFailSubscribedTask(RPCModel rpcModel, NotifyListener listener) {
        Holder holder = new Holder(rpcModel, listener);
        failSubscribed.remove(holder);
    }


    public void addFailUnsubscribed(RPCModel rpcModel, NotifyListener listener) {
        Holder holder = new Holder(rpcModel, listener);
        FailUnsubscribedTask failUnsubscribedTask = failUnsubscribed.get(holder);
        if (Objects.nonNull(failUnsubscribedTask)) {
            return;
        }
        FailUnsubscribedTask newTask = new FailUnsubscribedTask(rpcModel, this, listener);
        failUnsubscribedTask = failUnsubscribed.putIfAbsent(holder, newTask);
        if (Objects.isNull(failUnsubscribedTask)) {
            // never has a retry task. then start a new task for retry.
            retryTimer.newTimeout(newTask, retryPeriod, TimeUnit.MILLISECONDS);
        }
    }

    public void removeFailUnsubscribed(RPCModel rpcModel, NotifyListener listener) {
        Holder holder = new Holder(rpcModel, listener);
        FailUnsubscribedTask failUnsubscribedTask = failUnsubscribed.get(holder);
        if (Objects.nonNull(failUnsubscribedTask)) {
            failUnsubscribedTask.cancel();
        }
    }

    public void removeFailUnsubscribedTask(RPCModel rpcModel, NotifyListener listener) {
        Holder holder = new Holder(rpcModel, listener);
        failUnsubscribed.remove(holder);
    }


    public Map<RPCModel, FailRegisteredTask> getFailRegistered() {
        return failRegistered;
    }

    public Map<RPCModel, FailUnRegisteredTask> getFailUnRegistered() {
        return failUnRegistered;
    }

    public Map<Holder, FailSubscribedTask> getFailSubscribed() {
        return failSubscribed;
    }

    public Map<Holder, FailUnsubscribedTask> getFailUnsubscribed() {
        return failUnsubscribed;
    }

    @Override
    public void register(RPCModel rpcModel) {
        super.register(rpcModel);
        removeFailedRegistered(rpcModel);
        removeFailedUnRegistered(rpcModel);

        try {
            // Sending a registration request to the server side
            doRegister(rpcModel);
        } catch (Throwable t) {
            // If the startup detection is opened, the Exception is thrown directly
            Throwable throwable = t;
            boolean check = CommonSwitcher.ENABLE_FAIL_BACK_REGISTRY_RETRY_CHECK.isOn() && rpcModel.getPort() != 0;
            boolean skipFailBack = throwable instanceof SkipFailBackWrapperException;
            if (check || skipFailBack) {
                if (skipFailBack) {
                    throwable = throwable.getCause();
                }
                throw new IllegalStateException("Failed to register " + rpcModel + " to registry " + getRegistryRpcContext().getRegistryInfo() + ", cause: " + throwable.getMessage(), throwable);
            } else {
                log.error("Failed to register " + rpcModel + ", waiting for retry, cause: " + throwable.getMessage(), throwable);
            }

            // Record a failed registration request to a failed list, retry regularly
            addFailedRegistered(rpcModel);
        }
    }

    @Override
    public void reExportRegister(RPCModel rpcModel) {
        super.register(rpcModel);
        removeFailedRegistered(rpcModel);
        removeFailedUnRegistered(rpcModel);

        try {
            // Sending a registration request to the server side
            doRegister(rpcModel);
        } catch (Throwable t) {
            if (!(t instanceof SkipFailBackWrapperException)) {
                throw new IllegalStateException("Failed to register (re-export) " + rpcModel + " to registry " + getRegistryRpcContext().getRegistryAddress() + ", cause: " + t.getMessage(), t);
            }
        }
    }


    @Override
    public void unregister(RPCModel rpcModel) {
        super.unregister(rpcModel);
        removeFailedRegistered(rpcModel);
        removeFailedUnRegistered(rpcModel);

        try {
            // Sending a cancellation request to the server side
            doUnregister(rpcModel);
        } catch (Throwable t) {
            // If the startup detection is opened, the Exception is thrown directly
            Throwable throwable = t;
            boolean check = CommonSwitcher.ENABLE_FAIL_BACK_REGISTRY_RETRY_CHECK.isOn() && rpcModel.getPort() != 0;
            boolean skipFailBack = throwable instanceof SkipFailBackWrapperException;

            if (check || skipFailBack) {
                if (skipFailBack) {
                    throwable = t.getCause();
                }
                throw new IllegalStateException("Failed to unregister " + rpcModel + " to registry " + getRegistryRpcContext().getRegistryAddress() + ", cause: " + throwable.getMessage(), throwable);
            } else {
                log.error("Failed to unregister " + rpcModel + ", waiting for retry, cause: " + throwable.getMessage(), throwable);
            }

            // Record a failed registration request to a failed list, retry regularly
            addFailedUnregistered(rpcModel);
        }
    }


    @Override
    public void reExportUnregister(RPCModel rpcModel) {
        super.unregister(rpcModel);
        removeFailedRegistered(rpcModel);
        removeFailedUnRegistered(rpcModel);

        try {
            // Sending a cancellation request to the server side
            doUnregister(rpcModel);
        } catch (Exception e) {
            if (!(e instanceof SkipFailBackWrapperException)) {
                throw new IllegalStateException("Failed to unregister(re-export) " + rpcModel + " to registry " + getRegistryRpcContext().getRegistryAddress() + ", cause: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void subscribe(RPCModel rpcModel, NotifyListener listener) {
        super.subscribe(rpcModel, listener);
        removeFailSubscribed(rpcModel, listener);

        try {
            // Sending a subscription request to the server side
            doSubscribe(rpcModel, listener);
        } catch (Throwable t) {
            // If the startup detection is opened, the Exception is thrown directly
            Throwable throwable = t;
            boolean check = CommonSwitcher.ENABLE_FAIL_BACK_REGISTRY_RETRY_CHECK.isOn() && rpcModel.getPort() != 0;
            boolean skipFailBack = throwable instanceof SkipFailBackWrapperException;

            if (check || skipFailBack) {
                if (skipFailBack) {
                    throwable = t.getCause();
                }
                throw new IllegalStateException("Failed to subscribe " + rpcModel + ", cause: " + throwable.getMessage(), throwable);
            } else {
                log.error("Failed to subscribe " + rpcModel + ", waiting for retry, cause: " + throwable.getMessage(), throwable);
            }

            // Record a failed registration request to a failed list, retry regularly
            addFailSubscribed(rpcModel, listener);
        }
    }

    @Override
    public void unsubscribe(RPCModel rpcModel, NotifyListener listener) {
        super.unsubscribe(rpcModel, listener);
        removeFailUnsubscribed(rpcModel, listener);

        try {
            // Sending a canceling subscription request to the server side
            doUnsubscribe(rpcModel, listener);
        } catch (Throwable t) {
            // If the startup detection is opened, the Exception is thrown directly
            Throwable throwable = t;
            boolean check = CommonSwitcher.ENABLE_FAIL_BACK_REGISTRY_RETRY_CHECK.isOn() && rpcModel.getPort() != 0;
            boolean skipFailBack = throwable instanceof SkipFailBackWrapperException;

            if (check || skipFailBack) {
                if (skipFailBack) {
                    throwable = t.getCause();
                }
                throw new IllegalStateException("Failed to unsubscribe " + rpcModel + " to registry " + getRegistryRpcContext().getRegistryAddress() + ", cause: " + throwable.getMessage(), throwable);
            } else {
                log.error("Failed to unsubscribe " + rpcModel + ", waiting for retry, cause: " + throwable.getMessage(), throwable);
            }

            // Record a failed registration request to a failed list, retry regularly
            addFailUnsubscribed(rpcModel, listener);
        }
    }


    @Override
    protected void notify(RPCModel rpcModel, NotifyListener listener, List<RPCModel> rpcModels) {
        AssertUtil.notNull(rpcModel, "FailBackRegistry notify url is null.");
        AssertUtil.notNull(listener, "FailBackRegistry notify listener is null.");

        try {
            doNotify(rpcModel, listener, rpcModels);
        } catch (Throwable t) {
            log.error("Failed to notify addresses for subscribe " + rpcModel + ", cause: " + t.getMessage(), t);
        }
    }

    protected void doNotify(RPCModel rpcModel, NotifyListener listener, List<RPCModel> rpcModels) {
        super.notify(rpcModel, listener, rpcModels);
    }

    /**
     * retry register template method.
     * @param rpcModel registry information
     */
    public abstract void doRegister(RPCModel rpcModel);

    /**
     * retry unRegistry template method.
     * @param rpcModel unRegistry information
     */
    public abstract void doUnregister(RPCModel rpcModel);

    /**
     * retry subscribe template method.
     * @param rpcModel      subscribe information
     * @param listener notify listener
     */
    public abstract void doSubscribe(RPCModel rpcModel, NotifyListener listener);

    /**
     * retry unsubscribe template method.
     * @param rpcModel       unsubscribe information
     * @param listener  notify listener
     */
    public abstract void doUnsubscribe(RPCModel rpcModel, NotifyListener listener);


    static class Holder {
        private final RPCModel rpcModel;

        private final NotifyListener notifyListener;

        public Holder(RPCModel rpcModel, NotifyListener listener) {
            AssertUtil.isTrue(Objects.nonNull(rpcModel) && Objects.nonNull(listener), "url or listener is null.");
            this.rpcModel = rpcModel;
            this.notifyListener = listener;
        }

        @Override
        public int hashCode() {
            return rpcModel.hashCode() + notifyListener.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Holder) {
                Holder h = (Holder) obj;
                return this.rpcModel.equals(h.rpcModel) && this.notifyListener.equals(h.notifyListener);
            } else {
                return false;
            }
        }

    }
}
