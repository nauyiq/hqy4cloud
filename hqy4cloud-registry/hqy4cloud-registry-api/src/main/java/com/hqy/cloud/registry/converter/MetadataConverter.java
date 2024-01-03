package com.hqy.cloud.registry.converter;

import com.hqy.cloud.registry.common.model.MetadataInfo;

import java.util.Map;

/**
 * MetadataConverter.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public interface MetadataConverter {

    /**
     * metadata convert to map.
     * @param metadataInfo {@link MetadataInfo}
     * @return             map of metadata
     */
    Map<String, String> convertMap(MetadataInfo metadataInfo);

    /**
     * metadata map convert to metadata info.
     * @param application  application name
     * @param metadataMap  metadata map
     * @return             {@link MetadataInfo}
     */
    MetadataInfo convertMetadataInfo(String application, Map<String, String> metadataMap);


}
