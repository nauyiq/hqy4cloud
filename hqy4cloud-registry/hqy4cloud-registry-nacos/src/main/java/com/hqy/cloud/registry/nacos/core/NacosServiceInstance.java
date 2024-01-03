package com.hqy.cloud.registry.nacos.core;

import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.cloud.registry.api.ServiceInstance;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.common.model.MetadataInfo;
import com.hqy.cloud.registry.common.model.RegistryInfo;
import com.hqy.cloud.registry.nacos.utils.NacosInstanceConvertUtil;
import com.hqy.cloud.util.AssertUtil;

/**
 * NacosServiceInstance.
 * @see com.hqy.cloud.registry.api.ServiceInstance
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public class NacosServiceInstance implements ServiceInstance {
    private Instance instance;
    private ApplicationModel model;

    public NacosServiceInstance(Instance instance, String group, RegistryInfo registryInfo, MetadataInfo metadataInfo) {
        AssertUtil.notNull(instance, "Nacos instance should not be null.");
        AssertUtil.notNull(registryInfo, "Nacos registry info should not be null.");
        AssertUtil.notNull(metadataInfo, "Nacos metadataInfo should not be null.");
        this.instance = instance;
        this.model = NacosInstanceConvertUtil.convert(instance, group, registryInfo, metadataInfo);
    }

    @Override
    public String gerServiceName() {
        return instance.getServiceName();
    }

    @Override
    public String getHost() {
        return getIp() + StrUtil.COLON + port();
    }

    @Override
    public String getIp() {
        return instance.getIp();
    }

    @Override
    public int port() {
        return instance.getPort();
    }

    @Override
    public boolean isHealthy() {
        return instance.isHealthy();
    }

    @Override
    public boolean isMaster() {
        MetadataInfo metadata = this.getMetadata();
        return metadata.isMaster();
    }

    @Override
    public ApplicationModel getApplicationModel() {
        return model;
    }

    @Override
    public MetadataInfo getMetadata() {
        return model.getMetadataInfo();
    }

    @Override
    public String getClusterName() {
        return instance.getClusterName();
    }

    public Instance getInstance() {
        return instance;
    }

    public void setModel(ApplicationModel model) {
        this.model = model;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }
}
