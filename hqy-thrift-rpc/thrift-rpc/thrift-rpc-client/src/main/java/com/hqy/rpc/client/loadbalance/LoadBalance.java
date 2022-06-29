package com.hqy.rpc.client.loadbalance;


import com.hqy.rpc.Invocation;
import com.hqy.rpc.Invoker;
import com.hqy.rpc.common.Metadata;

import java.util.List;

/**
 * rpc load balancing.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 17:28
 */
public interface LoadBalance {

    /**
     * select one invoker in list.
     * @param invokers   invokers
     * @param metadata   refer metadata
     * @param invocation invocation
     * @return selected invoker.
     */
    <T> Invoker<T> select(List<Invoker<T>> invokers, Metadata metadata, Invocation invocation);

}
