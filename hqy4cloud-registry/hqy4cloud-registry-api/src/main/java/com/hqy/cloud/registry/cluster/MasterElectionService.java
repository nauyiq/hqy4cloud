package com.hqy.cloud.registry.cluster;

import com.hqy.cloud.registry.api.ServiceInstance;

import java.util.List;

/**
 * MasterElectionService.
 * Provide master node election and discovery </p>
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
@FunctionalInterface
public interface MasterElectionService {

    /**
     * elect master instance
     * @param instances heath instance
     */
    void elect(List<ServiceInstance> instances);

}
