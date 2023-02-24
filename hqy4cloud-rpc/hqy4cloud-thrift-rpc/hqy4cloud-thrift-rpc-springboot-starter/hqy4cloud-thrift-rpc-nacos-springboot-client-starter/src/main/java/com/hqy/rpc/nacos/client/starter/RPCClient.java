package com.hqy.rpc.nacos.client.starter;

import com.hqy.rpc.api.InvocationCallback;
import com.hqy.rpc.cluster.ClusterMode;
import com.hqy.rpc.nacos.client.starter.support.NacosThriftRPCClient;

/**
 * Client of RPC service for remote invocation.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 17:23
 */
public final class RPCClient {

    public static <T> T getRemoteService(Class<T> serviceClass) {
        return NacosThriftRPCClient.getInstance().getRemoteService(serviceClass);
    }

    public static <T> T getRemoteService(Class<T> serviceClass, String hashFactor) {
        return NacosThriftRPCClient.getInstance().getRemoteService(serviceClass, hashFactor);
    }
    public static <T> T getRemoteService(Class<T> serviceClass, String hashFactor, InvocationCallback invocationCallback) {
        return NacosThriftRPCClient.getInstance().getRemoteService(serviceClass, hashFactor, invocationCallback);
    }

    public static <T> T getRemoteService(Class<T> serviceClass, String hashFactor, InvocationCallback invocationCallback, ClusterMode clusterMode) {
        return NacosThriftRPCClient.getInstance().getRemoteService(serviceClass, hashFactor, invocationCallback, clusterMode);
    }


}
