package com.hqy.cloud.registry.api;

import com.hqy.cloud.registry.common.context.CloseableService;
import com.hqy.cloud.registry.common.exeception.RegisterDiscoverException;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.common.model.RegistryInfo;

import java.util.List;

import static com.hqy.cloud.registry.common.Constants.DEFAULT_DELAY_NOTIFICATION_TIME;
import static com.hqy.cloud.registry.common.Constants.REGISTRY_DELAY_NOTIFICATION_KEY;

/**
 * Registry, Entry to service registration and discovery
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 14:32
 */
public interface Registry extends ServerDiscovery, CloseableService {

    /**
     * registry name.
     * @return return registry name.
     */
    String name();

    /**
     * The registry information
     * @return {@link  RegistryInfo}
     */
    RegistryInfo getRegistryInfo();


    /**
     * Query the registered data that matches the conditions. Corresponding to the push mode of the subscription, this is the pull mode and returns only one result.
     * @param model Query condition
     * @return The registered information list, which may be empty, the meaning is the same as the parameters of {@link ServiceNotifyListener#notify(List)}.
     * @throws RegisterDiscoverException e.
     */
    List<ServiceInstance> lookup(ApplicationModel model) throws RegisterDiscoverException;

    /**
     * Query the registered data that matches the conditions. Corresponding to the push mode of the subscription, this is the pull mode and returns only one result.
     * @param model Query condition
     * @return The registered information list, which may be empty, the meaning is the same as the parameters of {@link ServiceNotifyListener#notify(List)}.
     * @throws RegisterDiscoverException e.
     */
    List<ServiceInstance> lookupAll(ApplicationModel model) throws RegisterDiscoverException;


    /**
     * Query the registered data that matches the conditions. Corresponding to the push mode of the subscription, this is the pull mode and returns only one result.
     * @param model Query condition
     * @return The registered information list, which may be empty, the meaning is the same as the parameters of {@link ServiceNotifyListener#notify(List)}.
     * @throws RegisterDiscoverException e.
     */
    List<ApplicationModel> lookupModels(ApplicationModel model) throws RegisterDiscoverException;

    /**
     * return registry delay notify time
     * @return delay notify time
     */
    default int getNotifyDelay() {
        return getModel().getParameter(REGISTRY_DELAY_NOTIFICATION_KEY, DEFAULT_DELAY_NOTIFICATION_TIME);
    }

    /**
     * notify subscribe service instance.
     * @param serviceInstances
     */
//    void notify(List<ServiceInstance> serviceInstances);

}
