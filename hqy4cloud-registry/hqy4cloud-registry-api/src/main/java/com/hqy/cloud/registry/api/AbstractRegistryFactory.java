package com.hqy.cloud.registry.api;

import com.hqy.cloud.registry.api.support.RegistryManager;
import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import com.hqy.cloud.registry.common.model.RegistryInfo;
import com.hqy.cloud.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractRegistryFactory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 16:21
 */
public abstract class AbstractRegistryFactory implements RegistryFactory {
    private static final Logger log = LoggerFactory.getLogger(AbstractRegistryFactory.class);

    @Override
    public Registry getRegistry(ProjectInfoModel model) {
        AssertUtil.notNull(model, "Application model should not be null.");
        // registry info.
        RegistryInfo info = model.getRegistryInfo();
        String key = info.getDesc();
        Registry registry = RegistryManager.getInstance().getRegistry(key);
        if (registry == null) {
            RegistryManager.getInstance().getRegistryLock().lock();
            try {
                //double check
                registry = RegistryManager.getInstance().getRegistry(key);
                if (registry != null) {
                    return registry;
                }
                registry = createRegistry(model);
            } catch (Throwable cause) {
                log.error("Failed execute to create registry, Application Model {}.", model);
                throw new RuntimeException("Failed execute to create registry", cause);
            } finally {
                // Release the lock
                RegistryManager.getInstance().getRegistryLock().unlock();
            }
        }
        if (registry != null) {
            RegistryManager.getInstance().putRegistry(key, registry);
        }
        return registry;
    }

    /**
     * create registry
     * @param model application model
     * @return      registry
     */
    protected abstract Registry createRegistry(ProjectInfoModel model);
}
