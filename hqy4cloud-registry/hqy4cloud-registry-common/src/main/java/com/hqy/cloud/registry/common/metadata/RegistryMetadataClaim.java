package com.hqy.cloud.registry.common.metadata;

import java.util.Map;

/**
 * RegistryMetadataClaim.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/8
 */
public interface RegistryMetadataClaim {
    int DEFAULT_PRIORITY = 0;

    /**
     * claim metadata to registry.
     * @param metadata    metadata
     * @param metadataMap write metadataMap to MetadataInfo
     * @return            {@link MetadataInfo}
     */
    MetadataInfo claim(MetadataInfo metadata, Map<String, String> metadataMap);


}
