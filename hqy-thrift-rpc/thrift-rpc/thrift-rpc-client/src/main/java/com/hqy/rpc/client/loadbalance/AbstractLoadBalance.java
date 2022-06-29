package com.hqy.rpc.client.loadbalance;

import com.hqy.rpc.Invocation;
import com.hqy.rpc.Invoker;
import com.hqy.rpc.common.Metadata;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * AbstractLoadBalance.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 17:38
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    /**
     * child class impl -> select one invoker in list.
     * @param invokers   invokers
     * @param metadata   refer metadata
     * @param invocation invocation
     * @return selected invoker.
     */
    protected abstract <T> Invoker<T> doSelect(List<Invoker<T>> invokers, Metadata metadata, Invocation invocation);


    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, Metadata metadata, Invocation invocation) {
        if (CollectionUtils.isEmpty(invokers)) {
            return null;
        }
        if (invokers.size() == 1) {
            return invokers.get(0);
        }
        return doSelect(invokers, metadata, invocation);
    }
}
