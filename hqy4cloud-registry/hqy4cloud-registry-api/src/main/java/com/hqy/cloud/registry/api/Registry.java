package com.hqy.cloud.registry.api;

import com.hqy.cloud.registry.common.model.RegistryInfo;

/**
 * Registry, Entry to service registration and discovery
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 14:32
 */
public interface Registry extends ServerDiscovery {

    /**
     * registry name.
     * @return return registry name.
     */
    String name();

    /**
     * The registry information
     * @return {@link  RegistryInfo}
     */
    RegistryInfo getRegistryInfo();

}
