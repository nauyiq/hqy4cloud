package com.hqy.cloud.rpc.cluster;

import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.cluster.directory.Directory;

/**
 * This is the final Invoker type referenced by the RPC proxy on Consumer side.
 * <p>
 * A ClusterInvoker holds a group of normal invokers, stored in a Directory, mapping to one Registry.
 * The ClusterInvoker implementation usually provides LB or HA policies, like FailoverClusterInvoker.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13
 */
public interface ClusterInvoker<T> extends Invoker<T> {

    /**
     * get directory.
     * @return {@link Directory}
     */
    Directory<T> getDirectory();

    /**
     * destroyed?
     * @return destroyed?
     */
    boolean isDestroyed();

}
