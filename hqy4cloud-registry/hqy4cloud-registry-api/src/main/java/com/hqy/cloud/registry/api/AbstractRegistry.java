package com.hqy.cloud.registry.api;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.registry.common.deploy.Deployer;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.common.model.MetadataInfo;
import com.hqy.cloud.registry.common.model.RegistryInfo;
import com.hqy.cloud.util.AssertUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * common base registry.
 * @see com.hqy.cloud.registry.api.Registry
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 14:31
 */
public abstract class AbstractRegistry implements Registry {
    private static final Logger log = LoggerFactory.getLogger(AbstractRegistry.class);

    /**
     * application deployer
     */
    private final Deployer<?> deployer;

    /**
     * registry information.
     */
    private final RegistryInfo registryInfo;

    /**
     * current service model.
     */
    private final ApplicationModel model;

    /**
     * current service instance.
     */
    private volatile ServiceInstance instance;

    /**
     * same cluster registered map
     */
    private final Set<ServiceInstance> clusterRegistered = new ConcurrentHashSet<>();

    /**
     * registry node set.
     */
    private final Set<ServiceInstance> registered = new ConcurrentHashSet<>();

    /**
     * subscribed service instance of listeners
     */
    private final Map<ServiceInstance, Set<ServiceNotifyListener>> subscribed = new ConcurrentHashMap<>();

    /**
     * notify list
     */
    private final Map<ServiceInstance, List<ServiceInstance>> notified = new ConcurrentHashMap<>();

    public AbstractRegistry(ApplicationModel model, Deployer<?> deployer) {
        AssertUtil.notNull(model, "Registry application model should not be null.");
        AssertUtil.notNull(deployer, "Service deployer  should not be null.");
        this.deployer = deployer;
        this.model = model;
        this.registryInfo = model.getRegistryInfo();
    }

    private synchronized ServiceInstance currentInstance() {
        return querySelfInstanceInfo();
    }

    public Deployer<?> getDeployer() {
        return deployer;
    }

    @Override
    public RegistryInfo getRegistryInfo() {
        return registryInfo;
    }

    @Override
    public ApplicationModel selfModel() {
        return this.model;
    }

    @Override
    public ServiceInstance selfInstance() {
        if (instance == null && deployer.isRunning()) {
            instance = currentInstance();
        }
        return instance;
    }

    @Override
    public MetadataInfo selfMetadataInfo() {
        return this.model.getMetadataInfo();
    }

    @Override
    public synchronized void register() throws RuntimeException {
        // check enable using register api.
        if (deployer.isPending()) {
            this.instance = doRegister();
        }
    }

    @Override
    public synchronized void unRegister() throws RuntimeException {
        // check enable using unRegister api.
        if (!deployer.isPending() || !deployer.isStopping() || !deployer.isStopped()) {
            doUnRegister();
        }
    }

    @Override
    public void register(ServiceInstance instance) {
        AssertUtil.notNull(instance, "Register instance should not be null.");
        registered.add(instance);
    }

    @Override
    public void unregister(ServiceInstance instance) {
        AssertUtil.notNull(instance, "Register instance should not be null.");
        registered.remove(instance);
    }

    @Override
    public void subscribe(ServiceInstance instance, ServiceNotifyListener serviceNotifyListener) {
        AssertUtil.notNull(instance, "subscribe instance should not be  null.");
        log.info("subscribe instance: {}", instance);
        Set<ServiceNotifyListener> listeners = subscribed.get(instance);
        if (CollectionUtils.isNotEmpty(listeners) && serviceNotifyListener != null) {
            listeners.remove(serviceNotifyListener);
        }
    }

    @Override
    public void unsubscribe(ServiceInstance instance, ServiceNotifyListener serviceNotifyListener) {
        AssertUtil.notNull(instance, "unSubscribe instance should not be  null.");
        log.info("unSubscribe instance: {}", instance);
        if (serviceNotifyListener != null) {
            Set<ServiceNotifyListener> listeners = subscribed.computeIfAbsent(instance, v -> new ConcurrentHashSet<>());
            listeners.add(serviceNotifyListener);
        }
        // do not forget remove notified
        notified.remove(instance);
    }

    @Override
    public void destroy() throws Exception {
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("Destroy registry: {}", getRegistryInfo());
        }
        Set<ServiceInstance> destroyRegistered = new HashSet<>(getRegistered());
        if (!destroyRegistered.isEmpty()) {
            for (ServiceInstance instance : destroyRegistered) {
                try {
                    unregister(instance);
                } catch (Throwable t) {
                    log.warn("Failed to unregister instance " + instance + " to registry " + getRegistryInfo() + " on destroy, cause: " + t.getMessage(), t);
                }

            }
        }

        Map<ServiceInstance, Set<ServiceNotifyListener>> destroySubscribed = new HashMap<>(getSubscribed());
        if (!destroySubscribed.isEmpty()) {
            for (Map.Entry<ServiceInstance, Set<ServiceNotifyListener>> entry : destroySubscribed.entrySet()) {
                ServiceInstance ServiceInstance = entry.getKey();
                for (ServiceNotifyListener notifyListener : entry.getValue()) {
                    try {
                        unsubscribe(ServiceInstance, notifyListener);
                    } catch (Throwable t) {
                        log.warn("Failed to unsubscribe instance " + instance + " to registry " + getRegistryInfo() + " on destroy, cause: " + t.getMessage(), t);
                    }
                }
            }
        }
        RegistryManager.getInstance().removeDestroyedRegistry(this);
    }

    protected void notify(List<ServiceInstance> serviceInstances) {
        if (CollectionUtils.isEmpty(serviceInstances)) {
            log.info("notify urls is empty.");
            return;
        }
        Set<Map.Entry<ServiceInstance, Set<ServiceNotifyListener>>> entries = getSubscribed().entrySet();
        for (Map.Entry<ServiceInstance, Set<ServiceNotifyListener>> entry : entries) {
            ServiceInstance serviceInstance = entry.getKey();
            Set<ServiceNotifyListener> listeners = entry.getValue();
            if (CollectionUtils.isNotEmpty(listeners)) {
                for (ServiceNotifyListener listener : listeners) {
                    try {
                        notify(serviceInstance, listener, serviceInstances);
                    } catch (Throwable t) {
                        log.error("Failed to notify registry event, urls: {}, cause: {}, {}", serviceInstances, t.getMessage(), t);
                    }
                }
            }
        }
    }

    protected void notify(ServiceInstance serviceInstance, ServiceNotifyListener listener, List<ServiceInstance> serviceInstances) {
        AssertUtil.notNull(serviceInstance, "notify instance should not be null.");
        AssertUtil.notNull(listener, "notify listener should not be null.");
        if (CollectionUtils.isEmpty(serviceInstances)) {
            log.warn("Ignore empty notify instance for subscribe url {}", serviceInstances);
        }
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("Notify urls for subscribe instance {}, url size {}", serviceInstance, serviceInstances.size());
        }
        listener.notify(serviceInstances);
        notified.put(serviceInstance, serviceInstances);
    }

    private Map<ServiceInstance, Set<ServiceNotifyListener>> getSubscribed() {
        return subscribed;
    }

    public Set<ServiceInstance> getRegistered() {
        return registered;
    }

    /**
     * query current instance from registry.
     * @return current self instance.
     */
    protected abstract ServiceInstance querySelfInstanceInfo();

    /**
     * do register self service.
     * @return self ServiceInstance
     */
    protected abstract ServiceInstance doRegister();

    /**
     *  do unRegister self service.
     */
    protected abstract void doUnRegister();



}
