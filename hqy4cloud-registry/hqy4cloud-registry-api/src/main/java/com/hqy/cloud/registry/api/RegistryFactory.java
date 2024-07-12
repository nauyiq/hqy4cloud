package com.hqy.cloud.registry.api;

import com.hqy.cloud.registry.common.model.ProjectInfoModel;

/**
 * RegistryFactory
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 16:19
 */
public interface RegistryFactory {

    /**
     * create registry instance by connect info
     * @param model the service application model.
     * @return      registry
     */
    Registry getRegistry(ProjectInfoModel model);

}
