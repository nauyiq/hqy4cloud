package com.hqy.rpc.registry.api;

import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.registry.api.support.RegistryManager;
import com.hqy.cloud.util.AssertUtil;
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
    public Registry getRegistry(RPCModel rpcModel) {
        AssertUtil.notNull(rpcModel, "Failed execute to getRegistry, rpcContext is null.");

        Registry defaultNopRegistry = RegistryManager.getInstance().getDefaultNopRegistryIfDestroyed();
        if (defaultNopRegistry != null) {
            return defaultNopRegistry;
        }
        Registry registry;
        String registryAddress = rpcModel.getRegistryAddress();
        RegistryManager.getInstance().getRegistryLock().lock();
        try {
            //double check
            defaultNopRegistry = RegistryManager.getInstance().getDefaultNopRegistryIfDestroyed();
            if (defaultNopRegistry != null) {
                return defaultNopRegistry;
            }
            registry = RegistryManager.getInstance().getRegistry(registryAddress);
            if (registry != null) {
                return registry;
            }
            registry = createRegistry(rpcModel);
        } catch (Throwable t) {
            log.error("Failed execute to create registry. rpcContext {}", rpcModel);
            throw new RuntimeException("Can not create Registry.");
        } finally {
            // Release the lock
            RegistryManager.getInstance().getRegistryLock().unlock();
        }

        if (registry != null) {
            RegistryManager.getInstance().putRegistry(registryAddress, registry);
        }

        return registry;
    }

    /**
     * create registry
     * @param rpcModel metadata.
     * @return           {@link RPCModel}
     */
    protected abstract Registry createRegistry(RPCModel rpcModel);
}
