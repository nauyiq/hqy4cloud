package com.hqy.cloud.rpc.cluster.router;

import com.hqy.cloud.rpc.Invocation;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.model.RpcModel;

import java.util.List;

/**
 * Router.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/30 16:34
 */
public interface Router<T> extends Comparable<Router<T>> {

    int DEFAULT_PRIORITY = Integer.MAX_VALUE;

    /**
     * get router metadata.
     * @return metadata
     */
    RpcModel getRpcModel();

    /**
     * route conditional invokers
     * @param invokers          invokers
     * @param invocation        this request invoker condition
     * @return                  router result
     */
    RouterResult<Invoker<T>> route(List<Invoker<T>> invokers, Invocation invocation);


    /**
     * Router's priority, used to sort routers.
     * @return router's priority
     */
    default int getPriority() {
        return DEFAULT_PRIORITY;
    }

    /**
     * Notify the router the invoker list. Invoker list may change from time to time. This method gives the router a
     * chance to prepare before {@link Router#route(List, Invocation)} gets called.
     *
     * @param invokers invoker list
     */
    default void notify(List<Invoker<T>> invokers) {

    }

    default void stop() {
        //do nothing by default
    }


}
