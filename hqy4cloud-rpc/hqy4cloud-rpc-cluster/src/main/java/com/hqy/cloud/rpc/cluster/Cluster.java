package com.hqy.cloud.rpc.cluster;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.cluster.directory.Directory;

/**
 * Cluster.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/1 15:58
 */
public interface Cluster {

    ClusterMode DEFAULT = ClusterMode.FAILOVER;

    /**
     * Merge the directory invokers to a virtual invoker.
     * @param directory     {@link Directory}
     * @return              {@link Invoker}
     * @throws RpcException
     */
    <T> Invoker<T> join(Directory<T> directory) throws RpcException;

    /**
     * Merge the directory invokers to a virtual invoker.
     * @param directory     {@link Directory}
     * @param hashFactor    use rpc cluster.
     * @return              {@link Invoker}
     * @throws RpcException
     */
    <T> Invoker<T> join(Directory<T> directory, String hashFactor) throws RpcException;



}
