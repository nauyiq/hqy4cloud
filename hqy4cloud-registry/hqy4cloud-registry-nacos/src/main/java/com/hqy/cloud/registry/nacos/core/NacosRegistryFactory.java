package com.hqy.cloud.registry.nacos.core;

import com.hqy.cloud.registry.api.AbstractRegistryFactory;
import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.common.metadata.support.DefaultMetadataConverter;
import com.hqy.cloud.registry.common.metadata.MetadataConverter;
import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import com.hqy.cloud.registry.nacos.utils.NacosNamingServiceUtil;

/**
 * NacosRegistryFactory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public class NacosRegistryFactory extends AbstractRegistryFactory {
    private final MetadataConverter metadataConverter;

    public NacosRegistryFactory() {
        this(new DefaultMetadataConverter());
    }

    public NacosRegistryFactory(MetadataConverter metadataConverter) {
        this.metadataConverter = metadataConverter;
    }

    @Override
    protected Registry createRegistry(ProjectInfoModel model) {
        return new NacosRegistry(model, NacosNamingServiceUtil.getNamingService(model), metadataConverter);
    }
}
