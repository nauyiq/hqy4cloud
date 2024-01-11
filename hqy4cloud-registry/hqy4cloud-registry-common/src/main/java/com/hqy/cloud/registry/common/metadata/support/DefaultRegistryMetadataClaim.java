package com.hqy.cloud.registry.common.metadata.support;

import com.hqy.cloud.registry.common.metadata.AbstractRegistryMetadataClaim;
import com.hqy.cloud.registry.common.metadata.MetadataInfo;
import com.hqy.cloud.registry.common.metadata.RegistryMetadataClaim;

import java.util.Map;

/**
 * DefaultRegistryMetadataClaim.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/8
 */
public class DefaultRegistryMetadataClaim extends AbstractRegistryMetadataClaim {
    private DefaultRegistryMetadataClaim() {

    }
    public static final RegistryMetadataClaim DEFAULT = new DefaultRegistryMetadataClaim();

    @Override
    public MetadataInfo claim(MetadataInfo metadata, Map<String, String> metadataMap) {
        metadata.addParameters(metadataMap);
        return metadata;
    }



}
