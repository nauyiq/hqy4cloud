package com.hqy.rpc.registry.api.support;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.common.Node;
import com.hqy.rpc.registry.api.NotifyListener;
import com.hqy.rpc.registry.api.Registry;
import com.hqy.util.AssertUtil;
import com.hqy.util.spring.ProjectContextInfo;
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
    private Metadata registryMetadata;

    /**
     * registry manager center
     */
    protected RegistryManager registryManager;

    private final Set<Node> registeredNode = new ConcurrentHashSet<>();

    /**
     * registry metadata set
     */
    private final Set<Metadata> registered = new ConcurrentHashSet<>();

    /**
     * key:consumer url, value:subscribe listener list
     */
    private final ConcurrentMap<Metadata, Set<NotifyListener>> subscribed = new ConcurrentHashMap<>();
    /**
     * key:consumer url, value: notify url list
     */
    private final ConcurrentMap<Metadata, List<Metadata>> notified = new ConcurrentHashMap<>();


    public AbstractRegistry(Metadata metadata) {
        setUrl(metadata);
        registryManager = ProjectContextInfo.getBean(RegistryManager.class);
    }

    @Override
    public Metadata getMetadata() {
        return registryMetadata;
    }

    protected void setUrl(Metadata metadata) {
        AssertUtil.notNull(metadata, "registry url is null.");
        this.registryMetadata = metadata;
    }

    public Set<Metadata> getRegistered() {
        return registered;
    }

    public ConcurrentMap<Metadata, Set<NotifyListener>> getSubscribed() {
        return subscribed;
    }

    public ConcurrentMap<Metadata, List<Metadata>> getNotified() {
        return notified;
    }

    @Override
    public void register(Metadata metadata) {
        AssertUtil.notNull(metadata, "registry url is null.");
        if (metadata.getPort() != 0) {
            log.info("registry url: {}", metadata);
        }
        registered.add(metadata);
        registeredNode.add(metadata.getNode());
    }

    @Override
    public void unregister(Metadata metadata) {
        AssertUtil.notNull(metadata, "unregister url is null.");
        if (metadata.getPort() != 0) {
            log.info("unregister url: {}", metadata);
        }
        registered.remove(metadata);
        registeredNode.remove(metadata.getNode());
    }

    @Override
    public void subscribe(Metadata metadata, NotifyListener listener) {
        AssertUtil.notNull(metadata, "subscribe url is null.");
        AssertUtil.notNull(metadata, "subscribe listener is null.");
        log.info("subscribe metadata: {}", metadata);
        Set<NotifyListener> listeners = subscribed.computeIfAbsent(metadata, n -> new ConcurrentHashSet<>());
        listeners.add(listener);
    }

    @Override
    public void unsubscribe(Metadata metadata, NotifyListener listener) {
        AssertUtil.notNull(metadata, "unsubscribe metadata is null.");
        AssertUtil.notNull(metadata, "unsubscribe listener is null.");
        log.info("unsubscribe metadata: {}", metadata);
        Set<NotifyListener> listeners = subscribed.get(metadata);
        if (CollectionUtils.isNotEmpty(listeners)) {
            listeners.remove(listener);
        }
        // do not forget remove notified
        notified.remove(metadata);
    }

    protected void recover() throws Exception {
        // register
        Set<Metadata> recoverRegistered = new HashSet<>(getRegistered());
        if (!recoverRegistered.isEmpty()) {
            if (log.isInfoEnabled()) {
                log.info("Recover register metadata {}", recoverRegistered);
            }
            for (Metadata metadata : recoverRegistered) {
                register(metadata);
            }
        }

        // subscribe
        Map<Metadata, Set<NotifyListener>> recoverSubscribed = new HashMap<>(getSubscribed());
        if (!recoverSubscribed.isEmpty()) {
            if (log.isInfoEnabled()) {
                log.info("Recover subscribe metadata {}", recoverSubscribed.keySet());
            }
            for (Map.Entry<Metadata, Set<NotifyListener>> entry : recoverSubscribed.entrySet()) {
                Metadata metadata = entry.getKey();
                for (NotifyListener listener : entry.getValue()) {
                    subscribe(metadata, listener);
                }
            }
        }
    }

    protected void notify(List<Metadata> metadataList) {
        if (CollectionUtils.isEmpty(metadataList)) {
            log.info("notify urls is empty.");
            return;
        }
        Set<Map.Entry<Metadata, Set<NotifyListener>>> entries = getSubscribed().entrySet();
        for (Map.Entry<Metadata, Set<NotifyListener>> entry : entries) {
            Metadata metadata = entry.getKey();
            Set<NotifyListener> listeners = entry.getValue();
            if (CollectionUtils.isNotEmpty(listeners)) {
                for (NotifyListener listener : listeners) {
                    try {
                        notify(metadata, listener, metadataList);
                    } catch (Throwable t) {
                        log.error("Failed to notify registry event, urls: {}, cause: {}, {}", metadata, t.getMessage(), t);
                    }
                }
            }
        }
    }

    protected void notify(Metadata metadata, NotifyListener listener, List<Metadata> metadataList) {
        AssertUtil.notNull(metadata, "notify url is null.");
        AssertUtil.notNull(listener, "notify listener is null.");
        if (CollectionUtils.isEmpty(metadataList)) {
            log.warn("Ignore empty notify urls for subscribe url {}", metadata);
            return;
        }
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("Notify urls for subscribe url {}, url size {}", metadata, metadataList.size());
        }
        listener.notify(metadataList);
        notified.put(metadata, metadataList);
    }

    @Override
    public void destroy() {
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("Destroy registry: {}", getMetadata());
        }
        Set<Metadata> destroyRegistered = new HashSet<>(getRegistered());
        if (!destroyRegistered.isEmpty()) {
            for (Metadata metadata : destroyRegistered) {
                try {
                    unregister(metadata);
                    if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                        log.info("Destroy unregister url :{}", metadata);
                    }
                } catch (Throwable t) {
                    log.warn("Failed to unregister url " + metadata + " to registry " + getMetadata() + " on destroy, cause: " + t.getMessage(), t);
                }

            }
        }

        Map<Metadata, Set<NotifyListener>> destroySubscribed = new HashMap<>(getSubscribed());
        if (!destroySubscribed.isEmpty()) {
            for (Map.Entry<Metadata, Set<NotifyListener>> entry : destroySubscribed.entrySet()) {
                Metadata metadata = entry.getKey();
                for (NotifyListener notifyListener : entry.getValue()) {
                    try {
                        unsubscribe(metadata, notifyListener);
                        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                            log.info("Destroy unsubscribe url :{}", metadata);
                        }
                    } catch (Throwable t) {
                        log.warn("Failed to unsubscribe url " + metadata + " to registry " + getMetadata() + " on destroy, cause: " + t.getMessage(), t);
                    }
                }
            }
        }

        registryManager.removeDestroyedRegistry(this);

    }
}
