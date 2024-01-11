package com.hqy.cloud.rpc.cluster.support;

import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.cluster.AbstractCluster;
import com.hqy.cloud.rpc.cluster.directory.Directory;

import java.util.Map;

/**
 * {@link FailBackClusterInvoker}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13
 */
public class FailBackCluster extends AbstractCluster {

    @Override
    public <T> Invoker<T> doJoin(Directory<T> directory, Map<String, Object> attachments) {
        return new FailBackClusterInvoker<>(directory, attachments);
    }
}
