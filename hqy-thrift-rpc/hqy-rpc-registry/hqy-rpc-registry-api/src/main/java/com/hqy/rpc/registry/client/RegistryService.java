package com.hqy.rpc.registry.client;

import com.hqy.rpc.registry.api.NotifyListener;
import com.hqy.rpc.common.URL;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 11:06
 */
public interface RegistryService {


    /**
     * Register data, such as : provider service, consumer address, route rule, override rule and other data.
     * @param url Registration information
     */
    void register(URL url);


    /**
     * Unregister
     * @param url Registration information
     */
    void unregister(URL url);


    /**
     * Subscribe to eligible registered data and automatically push when the registered data is changed.
     * @param url       Subscription condition
     * @param listener  A listener of the change event
     */
    void subscribe(URL url, NotifyListener listener);


    /**
     * Unsubscribe
     * @param url      Subscription condition
     * @param listener A listener of the change event
     */
    void unsubscribe(URL url, NotifyListener listener);

}
