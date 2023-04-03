package com.hqy.cloud.rpc.cluster;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.cluster.directory.Directory;
import com.hqy.cloud.rpc.CommonConstants;

/**
 * AbstractCluster.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 9:57
 */
public abstract class AbstractCluster implements Cluster {

    @Override
    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        return doJoin(directory, CommonConstants.DEFAULT_HASH_FACTOR);
    }

    @Override
    public <T> Invoker<T> join(Directory<T> directory, String hashFactor) throws RpcException {
        return doJoin(directory, hashFactor);
    }

    /**
     * directory do join.
     * @param directory  {@link Directory}
     * @param hashFactor use rpc cluster.
     * @return           {@link Invoker}
     */
    public abstract <T> Invoker<T> doJoin(Directory<T> directory, String hashFactor);
}
