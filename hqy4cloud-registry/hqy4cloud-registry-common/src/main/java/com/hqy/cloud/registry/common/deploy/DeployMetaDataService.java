package com.hqy.cloud.registry.common.deploy;

import com.hqy.cloud.registry.common.metadata.MetadataService;
import com.hqy.cloud.registry.common.metadata.RegistryMetadataClaim;
import com.hqy.cloud.registry.common.metadata.support.DefaultRegistryMetadataClaim;

/**
 * DeployMetaDataService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/8
 */
public interface DeployMetaDataService extends MetadataService {
    int DEFAULT_PRIORITY = 0;

    /**
     * get this deploy model bind claim, enable is null
     * @return {@link RegistryMetadataClaim}
     */
    default RegistryMetadataClaim getMetaDataClaim() {
        return DefaultRegistryMetadataClaim.DEFAULT;
    }

    /**
     * setting priority
     * @return priority
     */
    default int getPriority() {
        return DEFAULT_PRIORITY;
    }

}
