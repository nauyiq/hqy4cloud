package com.hqy.rpc.cluster.support;

import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.cluster.AbstractCluster;
import com.hqy.rpc.cluster.directory.Directory;

/**
 * {@link FailFastClusterInvoker}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 13:50
 */
public class FailFastCluster extends AbstractCluster {

    @Override
    public <T> Invoker<T> doJoin(Directory<T> directory, String hashFactor) {
        return new FailFastClusterInvoker<>(directory, hashFactor);
    }
}
