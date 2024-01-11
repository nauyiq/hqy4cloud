package com.hqy.cloud.registry.common.metadata;

import java.util.Collections;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/9
 */
public interface MetadataService {

    /**
     * get metaData map
     * @return metaData map
     */
    default Map<String, String> getMetadataMap() {
        return Collections.emptyMap();
    }

}
