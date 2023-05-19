package com.hqy.cloud.canal.core.parser;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:43
 */
public interface ModelTableMetadataManager {

    /**
     * load ModelTableMetadata.
     * @param klass class type.
     * @return      {@link ModelTableMetadata}
     */
    ModelTableMetadata load(Class<?> klass);

}
