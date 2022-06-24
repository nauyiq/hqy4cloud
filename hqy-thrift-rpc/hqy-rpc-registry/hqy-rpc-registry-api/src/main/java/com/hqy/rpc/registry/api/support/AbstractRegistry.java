package com.hqy.rpc.registry.api.support;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.rpc.common.URL;
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
     * consumer registry url
     */
    private URL registryUrl;

    /**
     * registry manager center
     */
    protected RegistryManager registryManager;

    /**
     * registry url set
     */
    private final Set<URL> registered = new ConcurrentHashSet<>();

    /**
     * key:consumer url, value:subscribe listener list
     */
    private final ConcurrentMap<URL, Set<NotifyListener>> subscribed = new ConcurrentHashMap<>();
    /**
     * key:consumer url, value: notify url list
     */
    private final ConcurrentMap<URL, List<URL>> notified = new ConcurrentHashMap<>();


    public AbstractRegistry(URL url) {
        setUrl(url);
        registryManager = ProjectContextInfo.getBean(RegistryManager.class);
    }

    @Override
    public URL getUrl() {
        return registryUrl;
    }

    protected void setUrl(URL url) {
        AssertUtil.notNull(url, "registry url is null.");
        this.registryUrl = url;
    }

    public Set<URL> getRegistered() {
        return registered;
    }

    public ConcurrentMap<URL, Set<NotifyListener>> getSubscribed() {
        return subscribed;
    }

    public ConcurrentMap<URL, List<URL>> getNotified() {
        return notified;
    }

    @Override
    public void register(URL url) {
        AssertUtil.notNull(url, "registry url is null.");
        if (url.getPort() != 0) {
            log.info("registry url: {}", url);
        }
        registered.add(url);
    }

    @Override
    public void unregister(URL url) {
        AssertUtil.notNull(url, "unregister url is null.");
        if (url.getPort() != 0) {
            log.info("unregister url: {}", url);
        }
        registered.remove(url);
    }

    @Override
    public void subscribe(URL url, NotifyListener listener) {
        AssertUtil.notNull(url, "subscribe url is null.");
        AssertUtil.notNull(url, "subscribe listener is null.");
        log.info("subscribe url: {}", url);
        Set<NotifyListener> listeners = subscribed.computeIfAbsent(url, n -> new ConcurrentHashSet<>());
        listeners.add(listener);
    }

    @Override
    public void unsubscribe(URL url, NotifyListener listener) {
        AssertUtil.notNull(url, "unsubscribe url is null.");
        AssertUtil.notNull(url, "unsubscribe listener is null.");
        log.info("unsubscribe url: {}", url);
        Set<NotifyListener> listeners = subscribed.get(url);
        if (CollectionUtils.isNotEmpty(listeners)) {
            listeners.remove(listener);
        }
        // do not forget remove notified
        notified.remove(url);
    }

    protected void recover() throws Exception {
        // register
        Set<URL> recoverRegistered = new HashSet<>(getRegistered());
        if (!recoverRegistered.isEmpty()) {
            if (log.isInfoEnabled()) {
                log.info("Recover register url {}", recoverRegistered);
            }
            for (URL url : recoverRegistered) {
                register(url);
            }
        }

        // subscribe
        Map<URL, Set<NotifyListener>> recoverSubscribed = new HashMap<>(getSubscribed());
        if (!recoverSubscribed.isEmpty()) {
            if (log.isInfoEnabled()) {
                log.info("Recover subscribe url {}", recoverSubscribed.keySet());
            }
            for (Map.Entry<URL, Set<NotifyListener>> entry : recoverSubscribed.entrySet()) {
                URL url = entry.getKey();
                for (NotifyListener listener : entry.getValue()) {
                    subscribe(url, listener);
                }
            }
        }
    }

    protected void notify(List<URL> urls) {
        if (CollectionUtils.isEmpty(urls)) {
            log.info("notify urls is empty.");
            return;
        }
        Set<Map.Entry<URL, Set<NotifyListener>>> entries = getSubscribed().entrySet();
        for (Map.Entry<URL, Set<NotifyListener>> entry : entries) {
            URL url = entry.getKey();
            Set<NotifyListener> listeners = entry.getValue();
            if (CollectionUtils.isNotEmpty(listeners)) {
                for (NotifyListener listener : listeners) {
                    try {
                        notify(url, listener, urls);
                    } catch (Throwable t) {
                        log.error("Failed to notify registry event, urls: {}, cause: {}, {}", urls, t.getMessage(), t);
                    }
                }
            }
        }
    }

    protected void notify(URL url, NotifyListener listener, List<URL> urls) {
        AssertUtil.notNull(url, "notify url is null.");
        AssertUtil.notNull(listener, "notify listener is null.");
        if (CollectionUtils.isEmpty(urls)) {
            log.warn("Ignore empty notify urls for subscribe url {}", url);
            return;
        }
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("Notify urls for subscribe url {}, url size {}", url, urls.size());
        }
        listener.notify(urls);
        notified.put(url, urls);
    }

    @Override
    public void destroy() {
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("Destroy registry: {}", getUrl());
        }
        Set<URL> destroyRegistered = new HashSet<>(getRegistered());
        if (!destroyRegistered.isEmpty()) {
            for (URL url : destroyRegistered) {
                try {
                    unregister(url);
                    if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                        log.info("Destroy unregister url :{}", url);
                    }
                } catch (Throwable t) {
                    log.warn("Failed to unregister url " + url + " to registry " + getUrl() + " on destroy, cause: " + t.getMessage(), t);
                }

            }
        }

        Map<URL, Set<NotifyListener>> destroySubscribed = new HashMap<>(getSubscribed());
        if (!destroySubscribed.isEmpty()) {
            for (Map.Entry<URL, Set<NotifyListener>> entry : destroySubscribed.entrySet()) {
                URL url = entry.getKey();
                for (NotifyListener notifyListener : entry.getValue()) {
                    try {
                        unsubscribe(url, notifyListener);
                        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                            log.info("Destroy unsubscribe url :{}", url);
                        }
                    } catch (Throwable t) {
                        log.warn("Failed to unsubscribe url " + url + " to registry " + getUrl() + " on destroy, cause: " + t.getMessage(), t);
                    }
                }
            }
        }

        registryManager.removeDestroyedRegistry(this);

    }
}
