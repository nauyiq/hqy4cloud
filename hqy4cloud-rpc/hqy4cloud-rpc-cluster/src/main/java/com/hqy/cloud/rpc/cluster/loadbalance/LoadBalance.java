package com.hqy.cloud.rpc.cluster.loadbalance;


import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.model.RpcModel;

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
     * @param invokers    invokers
     * @param rpcModel  refer rpcContext
     * @return            selected invoker.
     */
    <T> Invoker<T> select(List<Invoker<T>> invokers, RpcModel rpcModel);

}
