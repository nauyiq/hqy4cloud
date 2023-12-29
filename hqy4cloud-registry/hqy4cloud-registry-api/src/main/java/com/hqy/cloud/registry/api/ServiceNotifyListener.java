package com.hqy.cloud.registry.api;

import java.util.List;

/**
 * NotifyListener.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 11:30
 */
public interface ServiceNotifyListener {

    /**
     * Triggered when a service change notification is received.
     * @param instances The list of registered instances
     */
    void notify(List<ServiceInstance> instances);


}
