package com.hqy.rpc.registry.client;

import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.registry.api.NotifyListener;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 11:06
 */
public interface RegistryService {


    /**
     * Register data, such as : provider service, consumer address, route rule, override rule and other data.
     * @param metadata Registration information
     */
    void register(Metadata metadata);


    /**
     * Unregister
     * @param metadata Registration information
     */
    void unregister(Metadata metadata);


    /**
     * Subscribe to eligible registered data and automatically push when the registered data is changed.
     * @param metadata  Subscription condition
     * @param listener  A listener of the change event
     */
    void subscribe(Metadata metadata, NotifyListener listener);


    /**
     * Unsubscribe
     * @param metadata Subscription condition
     * @param listener A listener of the change event
     */
    void unsubscribe(Metadata metadata, NotifyListener listener);

}
