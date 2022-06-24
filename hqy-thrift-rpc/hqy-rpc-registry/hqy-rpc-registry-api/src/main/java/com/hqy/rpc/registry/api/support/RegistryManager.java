package com.hqy.rpc.registry.api.support;

import com.hqy.rpc.registry.api.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 16:55
 */
public class RegistryManager {

    private static final Logger log = LoggerFactory.getLogger(RegistryManager.class);

    /**
     * Registry Collection Map<RegistryAddress, Registry>
     */
    private final Map<String, Registry> registries = new ConcurrentHashMap<>();

    /**
     * The lock for the acquisition process of the registry
     */
    protected final ReentrantLock lock = new ReentrantLock();

    private final AtomicBoolean destroyed = new AtomicBoolean(false);

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

}
