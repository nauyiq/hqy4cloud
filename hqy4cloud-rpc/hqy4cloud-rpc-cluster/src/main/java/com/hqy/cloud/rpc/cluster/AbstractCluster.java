package com.hqy.cloud.rpc.cluster;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.cluster.directory.Directory;

import java.util.Map;

/**
 * AbstractCluster.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13
 */
public abstract class AbstractCluster implements Cluster {


    @Override
    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        return this.join(directory, MapUtil.empty());
    }

    @Override
    public <T> Invoker<T> join(Directory<T> directory, Map<String, Object> attachments) throws RpcException {
        return doJoin(directory, attachments);
    }

    /**
     * directory do join.
     * @param directory   {@link Directory}
     * @param attachments attachments.
     * @return            {@link Invoker}
     */
    public abstract <T> Invoker<T> doJoin(Directory<T> directory, Map<String, Object> attachments);
}
