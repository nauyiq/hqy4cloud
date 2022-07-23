package com.hqy.rpc.cluster.support;

import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.cluster.AbstractCluster;
import com.hqy.rpc.cluster.directory.Directory;

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
