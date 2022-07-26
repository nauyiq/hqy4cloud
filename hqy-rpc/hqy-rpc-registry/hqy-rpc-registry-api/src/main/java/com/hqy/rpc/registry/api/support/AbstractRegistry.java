package com.hqy.rpc.registry.api.support;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.registry.api.NotifyListener;
import com.hqy.rpc.registry.api.Registry;
import com.hqy.util.AssertUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * AbstractRegistry  {@link Registry}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 18:02
 */
public abstract class AbstractRegistry implements Registry {

    private static final Logger log = LoggerFactory.getLogger(AbstractRegistry.class);

    /**
     * registry metadata
     */
    private RPCModel registryRpcModel;

    /**
     * registry node set.
     */
    private final Set<RPCModel> registered = new ConcurrentHashSet<>();

    /**
     * key:consumer url, value:subscribe listener list
     */
    private final ConcurrentMap<RPCModel, Set<NotifyListener>> subscribed = new ConcurrentHashMap<>();
    /**
     * key:consumer url, value: notify url list
     */
    private final ConcurrentMap<RPCModel, List<RPCModel>> notified = new ConcurrentHashMap<>();


    public AbstractRegistry(RPCModel rpcModel) {
        setMetadata(rpcModel);
    }

    public RPCModel getRegistryRpcContext() {
        return registryRpcModel;
    }

    protected void setMetadata(RPCModel rpcModel) {
        AssertUtil.notNull(rpcModel, "registry rpcContext is null.");
        this.registryRpcModel = rpcModel;
    }

    public Set<RPCModel> getRegistered() {
        return registered;
    }

    public ConcurrentMap<RPCModel, Set<NotifyListener>> getSubscribed() {
        return subscribed;
    }

    public ConcurrentMap<RPCModel, List<RPCModel>> getNotified() {
        return notified;
    }

    @Override
    public RPCModel getModel() {
        return registryRpcModel;
    }

    @Override
    public void register(RPCModel rpcModel) {
        if (rpcModel == null) {
            throw new IllegalArgumentException("register rpcContext is null.");
        }
        registered.add(rpcModel);
    }

    @Override
    public void unregister(RPCModel rpcModel) {
        AssertUtil.notNull(rpcModel, "register rpcContext is null.");
        registered.remove(rpcModel);
    }

    @Override
    public void subscribe(RPCModel rpcModel, NotifyListener listener) {
        AssertUtil.notNull(rpcModel, "subscribe url is null.");
        AssertUtil.notNull(rpcModel, "subscribe listener is null.");
        log.info("subscribe rpcContext: {}", rpcModel);
        Set<NotifyListener> listeners = subscribed.computeIfAbsent(rpcModel, n -> new ConcurrentHashSet<>());
        listeners.add(listener);
    }

    @Override
    public void unsubscribe(RPCModel rpcModel, NotifyListener listener) {
        AssertUtil.notNull(rpcModel, "unsubscribe rpcContext is null.");
        AssertUtil.notNull(rpcModel, "unsubscribe listener is null.");
        log.info("unsubscribe rpcContext: {}", rpcModel);
        Set<NotifyListener> listeners = subscribed.get(rpcModel);
        if (CollectionUtils.isNotEmpty(listeners)) {
            listeners.remove(listener);
        }
        // do not forget remove notified
        notified.remove(rpcModel);
    }

    protected void recover() throws Exception {
        // register
        Set<RPCModel> recoverRegistered = new HashSet<>(getRegistered());
        if (!recoverRegistered.isEmpty()) {
            if (log.isInfoEnabled()) {
                log.info("Recover register metadata {}", recoverRegistered);
            }
            for (RPCModel rpcModel : recoverRegistered) {
                register(rpcModel);
            }
        }

        // subscribe
        Map<RPCModel, Set<NotifyListener>> recoverSubscribed = new HashMap<>(getSubscribed());
        if (!recoverSubscribed.isEmpty()) {
            if (log.isInfoEnabled()) {
                log.info("Recover subscribe metadata {}", recoverSubscribed.keySet());
            }
            for (Map.Entry<RPCModel, Set<NotifyListener>> entry : recoverSubscribed.entrySet()) {
                RPCModel rpcModel = entry.getKey();
                for (NotifyListener listener : entry.getValue()) {
                    subscribe(rpcModel, listener);
                }
            }
        }
    }

    protected void notify(List<RPCModel> rpcModels) {
        if (CollectionUtils.isEmpty(rpcModels)) {
            log.info("notify urls is empty.");
            return;
        }
        Set<Map.Entry<RPCModel, Set<NotifyListener>>> entries = getSubscribed().entrySet();
        for (Map.Entry<RPCModel, Set<NotifyListener>> entry : entries) {
            RPCModel rpcModel = entry.getKey();
            Set<NotifyListener> listeners = entry.getValue();
            if (CollectionUtils.isNotEmpty(listeners)) {
                for (NotifyListener listener : listeners) {
                    try {
                        notify(rpcModel, listener, rpcModels);
                    } catch (Throwable t) {
                        log.error("Failed to notify registry event, urls: {}, cause: {}, {}", rpcModel, t.getMessage(), t);
                    }
                }
            }
        }
    }

    protected void notify(RPCModel rpcModel, NotifyListener listener, List<RPCModel> rpcModels) {
        AssertUtil.notNull(rpcModel, "notify rpcContext is null.");
        AssertUtil.notNull(listener, "notify listener is null.");
        if (CollectionUtils.isEmpty(rpcModels)) {
            log.warn("Ignore empty notify rpcContexts for subscribe url {}", rpcModel);
        }
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("Notify urls for subscribe rpcContexts {}, url size {}", rpcModel, rpcModels.size());
        }
        listener.notify(rpcModels);
        notified.put(rpcModel, rpcModels);
    }

    @Override
    public void destroy() {
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("Destroy registry: {}", getRegistryRpcContext());
        }
        Set<RPCModel> destroyRegistered = new HashSet<>(getRegistered());
        if (!destroyRegistered.isEmpty()) {
            for (RPCModel rpcModel : destroyRegistered) {
                try {
                    unregister(rpcModel);
                    if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                        log.info("Destroy unregister url :{}", rpcModel);
                    }
                } catch (Throwable t) {
                    log.warn("Failed to unregister rpcContext " + rpcModel + " to registry " + getRegistryRpcContext() + " on destroy, cause: " + t.getMessage(), t);
                }

            }
        }

        Map<RPCModel, Set<NotifyListener>> destroySubscribed = new HashMap<>(getSubscribed());
        if (!destroySubscribed.isEmpty()) {
            for (Map.Entry<RPCModel, Set<NotifyListener>> entry : destroySubscribed.entrySet()) {
                RPCModel rpcModel = entry.getKey();
                for (NotifyListener notifyListener : entry.getValue()) {
                    try {
                        unsubscribe(rpcModel, notifyListener);
                        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                            log.info("Destroy unsubscribe url :{}", rpcModel);
                        }
                    } catch (Throwable t) {
                        log.warn("Failed to unsubscribe url " + rpcModel + " to registry " + getRegistryRpcContext() + " on destroy, cause: " + t.getMessage(), t);
                    }
                }
            }
        }
        RegistryManager.getInstance().removeDestroyedRegistry(this);
    }
}
