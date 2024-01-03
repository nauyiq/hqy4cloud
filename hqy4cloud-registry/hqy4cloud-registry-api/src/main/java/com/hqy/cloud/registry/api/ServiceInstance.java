package com.hqy.cloud.registry.api;


import com.hqy.cloud.registry.cluster.ClusterService;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.common.model.MetadataInfo;

import java.io.Serializable;

/**
 * The model class of an instance of a service, which is used for service registration and discovery.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/28 17:42
 */
public interface ServiceInstance extends ClusterService, Serializable {

    /**
     * The name of service that current instance belongs to.
     * @return non-null
     */
    String gerServiceName();

    /**
     * The hostname of the registered service instance.
     * @return non-null
     */
    String getHost();

    /**
     * The port of the registered service instance. but not rpc port.
     * @return server port
     */
    int port();

    /**
     * return ip
     * @return non-null
     */
    String getIp();


    /**
     * The registered service instance is health or not.
     * @return indicates current instance is healthy, or unhealthy, the client may ignore this one.
     */
    default boolean isHealthy() {
        return true;
    }

    /**
     * The registered service instance is cluster master.
     * @return  indicates current instance is master,  or unhealthy, the client may ignore this one.
     */
    default boolean isMaster() {
        return false;
    }

    /**
     * The model of the current instance
     * @return non-null
     */
    ApplicationModel getApplicationModel();

    /**
     * The rpc metadata of the current instance
     * @return non-null
     */
    MetadataInfo getMetadata();


}
