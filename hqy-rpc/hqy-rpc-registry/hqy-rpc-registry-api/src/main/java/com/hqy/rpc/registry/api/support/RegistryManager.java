package com.hqy.rpc.registry.api.support;

import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.registry.api.NotifyListener;
import com.hqy.rpc.registry.api.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 16:55
 */
public class RegistryManager {

    private static final Logger log = LoggerFactory.getLogger(RegistryManager.class);

    private RegistryManager() {}

    private volatile static RegistryManager instance = null;

    public static RegistryManager getInstance() {
        if (instance == null) {
            synchronized (RegistryManager.class) {
                if (instance == null) {
                    instance = new RegistryManager();
                }
            }
        }
        return instance;
    }

    /**
     * Registry Collection Map<RegistryAddress, Registry>
     */
    private final Map<String, Registry> registries = new ConcurrentHashMap<>();

    /**
     * The lock for the acquisition process of the registry
     */
    protected final ReentrantLock lock = new ReentrantLock();

    private final AtomicBoolean destroyed = new AtomicBoolean(false);

    private static final Registry DEFAULT_NOP_REGISTRY = new Registry() {
        @Override
        public String getServiceNameEn() {
            return null;
        }

        @Override
        public Metadata getMetadata() {
            return null;
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public void destroy() {

        }

        @Override
        public void register(Metadata metadata) {

        }

        @Override
        public void unregister(Metadata metadata) {

        }

        @Override
        public void subscribe(Metadata metadata, NotifyListener listener) {

        }

        @Override
        public void unsubscribe(Metadata metadata, NotifyListener listener) {

        }

        @Override
        public List<Metadata> lookup(Metadata metadata) {
            return null;
        }
    };

    /**
     * Get all registries
     * @return all registries
     */
    public Collection<Registry> getRegistries() {
        return Collections.unmodifiableCollection(new LinkedList<>(registries.values()));
    }

    public Registry getRegistry(String key) {
        return registries.get(key);
    }

    public void putRegistry(String key, Registry registry) {
        registries.put(key, registry);
    }

    /**
     * Close all created registries
     */
    public void destroyAll() {
        if (!destroyed.compareAndSet(false, true)) {
            return;
        }

        if (log.isInfoEnabled()) {
            log.info("Close all registries " + getRegistries());
        }
        // Lock up the registry shutdown process
        lock.lock();
        try {
            for (Registry registry : getRegistries()) {
                try {
                    registry.destroy();
                } catch (Throwable e) {
                    log.warn(e.getMessage(), e);
                }
            }
            registries.clear();
        } finally {
            // Release the lock
            lock.unlock();
        }
    }

    public void removeDestroyedRegistry(Registry toRm) {
        lock.lock();
        try {
            registries.entrySet().removeIf(entry -> entry.getValue().equals(toRm));
        } finally {
            lock.unlock();
        }
    }

    public Lock getRegistryLock() {
        return lock;
    }

    public Registry getDefaultNopRegistryIfDestroyed() {
        if (destroyed.get()) {
            log.warn("All registry instances have been destroyed, failed to fetch any instance. " +
                    "Usually, this means no need to try to do unnecessary redundant resource clearance, all registries has been taken care of.");
            return DEFAULT_NOP_REGISTRY;
        }
        return null;
    }

}
