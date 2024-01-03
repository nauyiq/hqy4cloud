package com.hqy.cloud.registry.api;

import com.hqy.cloud.registry.common.exeception.RegisterDiscoverException;

/**
 * RegistryService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 10:19
 */
public interface RegistryService {

    /**
     * register application
     * @param instance  Registration instance.
     * @throws RegisterDiscoverException e.
     */
    void register(ServiceInstance instance) throws RegisterDiscoverException;

    /**
     * un register application by request application model.
     * @param instance UnRegistration instance
     * @throws RegisterDiscoverException e.
     */
    void unregister(ServiceInstance instance) throws RegisterDiscoverException;

    /**
     * subscribe
     * @param instance       subscribe instance
     * @param serviceNotifyListener A listener of the change event
     * @throws RegisterDiscoverException e.
     */
    void subscribe(ServiceInstance instance, ServiceNotifyListener serviceNotifyListener) throws RegisterDiscoverException;

    /**
     * Unsubscribe
     * @param instance       Unsubscribe instance
     * @param serviceNotifyListener A listener of the change event
     * @throws RegisterDiscoverException e.
     */
    void unsubscribe(ServiceInstance instance, ServiceNotifyListener serviceNotifyListener) throws RegisterDiscoverException;




}
