package com.hqy.cloud.registry.common.metadata;

import java.util.Map;

/**
 * MetadataConverter.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public interface MetadataConverter {

    /**
     * metadata map convert to metadata info.
     * @param application  application name
     * @param metadataMap  metadata map
     * @return             {@link MetadataInfo}
     */
    MetadataInfo convertMetadataInfo(String application, Map<String, String> metadataMap);


}
