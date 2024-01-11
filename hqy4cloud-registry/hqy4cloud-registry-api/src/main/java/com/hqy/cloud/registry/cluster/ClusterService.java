package com.hqy.cloud.registry.cluster;

import com.hqy.cloud.common.base.lang.StringConstants;

/**
 * Registry Cluster Service.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public interface ClusterService {

    /**
     * get registry cluster name.
     * @return cluster name
     */
    default String getClusterName() {
        return StringConstants.EMPTY;
    }



}
