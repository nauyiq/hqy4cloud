package com.hqy.cloud.registry.common.metadata;

import javax.annotation.Nonnull;

/**
 * sorted RegistryMetadataClaim.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/8
 */
public abstract class AbstractRegistryMetadataClaim implements RegistryMetadataClaim {

    @Override
    public int compareTo(@Nonnull RegistryMetadataClaim o) {
        return Integer.compare(this.getPriority(), o.getPriority());
    }
}
