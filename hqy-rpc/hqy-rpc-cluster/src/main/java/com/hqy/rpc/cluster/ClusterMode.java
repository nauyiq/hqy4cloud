package com.hqy.rpc.cluster;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 15:23
 */
public enum ClusterMode {

    /**
     * {@link com.hqy.rpc.cluster.support.FailoverClusterInvoker}
     */
    FAILOVER,

    /**
     * {@link com.hqy.rpc.cluster.support.FailSafeClusterInvoker}
     */
    FAILSAFE,

    /**
     * {@link com.hqy.rpc.cluster.support.FailFastClusterInvoker}
     */
    FAILFAST,

    /**
     * {@link com.hqy.rpc.cluster.support.FailBackClusterInvoker}
     */
    FAILBACK


}
