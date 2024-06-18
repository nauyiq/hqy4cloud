package com.hqy.cloud.registry.api.support;

import com.hqy.cloud.registry.api.ServiceInstance;
import com.hqy.cloud.registry.common.Constants;
import com.hqy.cloud.registry.common.metadata.MetadataInfo;
import com.hqy.cloud.registry.common.model.ProjectInfoModel;

import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/9
 */
public record ApplicationServiceInstance(
        ProjectInfoModel model) implements ServiceInstance {

    @Override
    public String gerServiceName() {
        return model.getApplicationName();
    }

    @Override
    public String getHost() {
        return model.getHost();
    }

    @Override
    public int port() {
        return model.getPort();
    }

    @Override
    public String getIp() {
        return model.getIp();
    }

    @Override
    public boolean isHealthy() {
        return model.isHealthy();
    }

    @Override
    public boolean isMaster() {
        MetadataInfo metadataInfo = model.getMetadataInfo();
        return metadataInfo.isMaster();
    }

    @Override
    public ProjectInfoModel getApplicationModel() {
        return model;
    }

    @Override
    public MetadataInfo getMetadata() {
        return model.getMetadataInfo();
    }

    @Override
    public String getClusterName() {
        return model.getParameter(Constants.REGISTRY_CLUSTER_KEY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationServiceInstance that = (ApplicationServiceInstance) o;
        return Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model);
    }
}
