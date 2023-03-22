package com.hqy.cloud.rpc.cluster.support;

import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.cluster.AbstractCluster;
import com.hqy.cloud.rpc.cluster.directory.Directory;

/**
 * {@link FailSafeClusterInvoker}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 14:25
 */
public class FailSafeCluster extends AbstractCluster {

    @Override
    public <T> Invoker<T> doJoin(Directory<T> directory, String hashFactor) {
        return new FailSafeClusterInvoker<>(directory, hashFactor);
    }
}
