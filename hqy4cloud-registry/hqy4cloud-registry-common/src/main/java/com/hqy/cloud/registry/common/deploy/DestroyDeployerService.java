package com.hqy.cloud.registry.common.deploy;

/**
 * DestroyDeployerService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/3
 */
public interface DestroyDeployerService {

    /**
     * Pre-processing before destroy model
     */
    void preDestroy();

    /**
     * Post-processing after destroy model
     */
    void postDestroy();

}
