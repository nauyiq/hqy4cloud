package com.hqy.rpc.cluster.loadbalance;


import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.common.support.RPCModel;

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
    <T> Invoker<T> select(List<Invoker<T>> invokers, RPCModel rpcModel);

}
