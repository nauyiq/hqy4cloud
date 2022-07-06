package com.hqy.rpc.registry.api;

import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.registry.api.support.RegistryManager;
import com.hqy.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractRegistryFactory.
 * @see com.hqy.rpc.registry.api.RegistryFactory
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/6 11:27
 */
public abstract class AbstractRegistryFactory implements RegistryFactory {

    private static final Logger log = LoggerFactory.getLogger(AbstractRegistryFactory.class);


    @Override
    public Registry getRegistry(Metadata metadata) {
        AssertUtil.notNull(metadata, "Failed execute to getRegistry, metadata is null.");

        Registry defaultNopRegistry = RegistryManager.getInstance().getDefaultNopRegistryIfDestroyed();
        if (defaultNopRegistry != null) {
            return defaultNopRegistry;
        }
        Registry registry;
        String serviceName = metadata.getServiceName();
        RegistryManager.getInstance().getRegistryLock().lock();
        try {
            //double check
            defaultNopRegistry = RegistryManager.getInstance().getDefaultNopRegistryIfDestroyed();
            if (defaultNopRegistry != null) {
                return defaultNopRegistry;
            }
            registry = RegistryManager.getInstance().getRegistry(serviceName);
            if (registry != null) {
                return registry;
            }
            registry = createRegistry(metadata);
        } catch (Throwable t) {
            log.error("Failed execute to create registry. metadata {}", metadata);
            throw new RuntimeException("Can not create Registry.");
        } finally {
            // Release the lock
            RegistryManager.getInstance().getRegistryLock().unlock();
        }

        if (registry != null) {
            RegistryManager.getInstance().putRegistry(serviceName, registry);
        }

        return registry;
    }

    /**
     * create registry
     * @param metadata metadata.
     * @return         com.hqy.rpc.common.Metadata
     */
    protected abstract Registry createRegistry(Metadata metadata);
}
