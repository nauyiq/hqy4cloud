package com.hqy.cloud.registry.cluster;

import com.hqy.cloud.registry.api.ServiceInstance;

/**
 * MasterService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
@FunctionalInterface
public interface MasterService {

    /**
     * Get the master instance in the cluster.
     * @return master instance
     */
    ServiceInstance getMasterInstance();

}
