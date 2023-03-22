package com.hqy.cloud.rpc.cluster.support;

import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.cluster.AbstractCluster;
import com.hqy.cloud.rpc.cluster.directory.Directory;

/**
 * {@link FailBackClusterInvoker}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 11:26
 */
public class FailBackCluster extends AbstractCluster {

    @Override
    public <T> Invoker<T> doJoin(Directory<T> directory, String hashFactor) {
        return new FailBackClusterInvoker<>(directory, hashFactor);
    }
}
