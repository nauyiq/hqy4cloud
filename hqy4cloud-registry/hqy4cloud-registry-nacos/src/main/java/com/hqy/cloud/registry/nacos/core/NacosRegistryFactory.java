package com.hqy.cloud.registry.nacos.core;

import com.hqy.cloud.registry.api.AbstractRegistryFactory;
import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.converter.MetadataConverter;
import com.hqy.cloud.registry.nacos.utils.NacosNamingServiceUtil;

/**
 * NacosRegistryFactory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public class NacosRegistryFactory extends AbstractRegistryFactory {
    private final MetadataConverter metadataConverter;

    public NacosRegistryFactory(MetadataConverter metadataConverter) {
        this.metadataConverter = metadataConverter;
    }

    @Override
    protected Registry createRegistry(ApplicationModel model) {
        return new NacosRegistry(model, NacosNamingServiceUtil.getNamingService(model), metadataConverter);
    }
}
