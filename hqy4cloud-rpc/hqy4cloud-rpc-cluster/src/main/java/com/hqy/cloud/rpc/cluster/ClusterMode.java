package com.hqy.cloud.rpc.cluster;

import com.hqy.cloud.rpc.cluster.support.FailBackClusterInvoker;
import com.hqy.cloud.rpc.cluster.support.FailFastClusterInvoker;
import com.hqy.cloud.rpc.cluster.support.FailSafeClusterInvoker;
import com.hqy.cloud.rpc.cluster.support.FailoverClusterInvoker;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 15:23
 */
public enum ClusterMode {

    /**
     * {@link FailoverClusterInvoker}
     */
    FAILOVER,

    /**
     * {@link FailSafeClusterInvoker}
     */
    FAILSAFE,

    /**
     * {@link FailFastClusterInvoker}
     */
    FAILFAST,

    /**
     * {@link FailBackClusterInvoker}
     */
    FAILBACK


}
