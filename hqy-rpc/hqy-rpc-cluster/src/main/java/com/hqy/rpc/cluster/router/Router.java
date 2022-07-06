package com.hqy.rpc.cluster.router;

import com.hqy.rpc.api.Invocation;
import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.common.Metadata;

import java.util.List;

/**
 * Router.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/30 16:34
 */
public interface Router extends Comparable<Router> {

    int DEFAULT_PRIORITY = Integer.MAX_VALUE;

    /**
     * get router metadata.
     * @return metadata
     */
    Metadata getMetadata();

    /**
     * route conditional invokers
     * @param invokers          invokers
     * @param metadata          metadata
     * @param invocation        invokers for invocation
     * @return                  router result
     */
    <T> RouterResult<Invoker<T>> route(List<Invoker<T>> invokers, Metadata metadata, Invocation invocation);


    /**
     * Router's priority, used to sort routers.
     * @return router's priority
     */
    default int getPriority() {
        return DEFAULT_PRIORITY;
    }

    /**
     * Notify the router the invoker list. Invoker list may change from time to time. This method gives the router a
     * chance to prepare before {@link Router#route(List, Metadata, Invocation)} gets called.
     *
     * @param invokers invoker list
     * @param <T>      invoker's type
     */
    default <T> void notify(List<Invoker<T>> invokers) {

    }

    default void stop() {
        //do nothing by default
    }


}
