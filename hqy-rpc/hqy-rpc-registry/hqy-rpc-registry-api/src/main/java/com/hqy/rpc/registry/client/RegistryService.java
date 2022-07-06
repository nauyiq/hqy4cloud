package com.hqy.rpc.registry.client;

import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.registry.api.NotifyListener;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 11:06
 */
public interface RegistryService {

    /**
     * get registry connectionInfo, assert connection not null.
     * @return ConnectionInfo
     */
//    ConnectionInfo getConnectionInfo();

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


    /**
     * Query the registered data that matches the conditions. Corresponding to the push mode of the subscription, this is the pull mode and returns only one result.
     * @param metadata Query condition
     * @return The registered information list, which may be empty, the meaning is the same as the parameters of {@link com.hqy.rpc.registry.api.NotifyListener#notify(List)}.
     * @see com.hqy.rpc.registry.api.NotifyListener#notify(List)
     */
    List<Metadata> lookup(Metadata metadata);

}
