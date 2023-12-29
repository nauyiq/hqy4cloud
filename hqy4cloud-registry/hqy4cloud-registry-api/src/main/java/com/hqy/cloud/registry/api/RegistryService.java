package com.hqy.cloud.registry.api;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 10:19
 */
public interface RegistryService {

    /**
     * register application
     * @param instance  Registration instance.
     */
    void register(ServiceInstance instance);

    /**
     * un register application by request application model.
     * @param instance UnRegistration instance
     */
    void unregister(ServiceInstance instance);

    /**
     * subscribe
     * @param instance       subscribe instance
     * @param serviceNotifyListener A listener of the change event
     */
    void subscribe(ServiceInstance instance, ServiceNotifyListener serviceNotifyListener);

    /**
     * Unsubscribe
     * @param instance       Unsubscribe instance
     * @param serviceNotifyListener A listener of the change event
     */
    void unsubscribe(ServiceInstance instance, ServiceNotifyListener serviceNotifyListener);




}
