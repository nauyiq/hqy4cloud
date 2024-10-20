package com.hqy.cloud.registry.api;

import com.hqy.cloud.registry.cluster.MasterService;
import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import com.hqy.cloud.registry.common.metadata.MetadataInfo;

/**
 * server discovery
 * Provide service registration and discovery </p>
 * Provides a local rpc perspective
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/28 17:19
 */
public interface ServerDiscovery extends RegistryService, MasterService {

    /**
     * return self rpc model
     * @return server application model {@link ProjectInfoModel}
     */
    ProjectInfoModel getModel();

    /**
     * return self metadata
     * @return metadata info {@link MetadataInfo}
     */
    MetadataInfo getMetadataInfo();

    /**
     * return self registered service instance.
     * @return registered service instance. {@link ServiceInstance}
     */
    ServiceInstance getInstance();

    /**
     * do register instance
     * @throws RuntimeException not try it
     */
    void register() throws RuntimeException;

    /**
     * do un register from registry.
     * @throws RuntimeException not try it
     */
    void unRegister() throws RuntimeException;

    /**
     * do update instance
     * @param model update model
     * @throws RuntimeException not try it
     */
    void update(ProjectInfoModel model) throws  RuntimeException;






}
