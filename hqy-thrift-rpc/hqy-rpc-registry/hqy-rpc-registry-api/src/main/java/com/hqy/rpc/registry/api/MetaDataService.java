package com.hqy.rpc.registry.api;

import com.hqy.rpc.registry.node.Metadata;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 16:30
 */
public interface MetaDataService {

    /**
     * get metadata
     * @return Metadata
     */
    Metadata getMetadata();

    /**
     * is available.
     * @return available.
     */
    boolean isAvailable();

    /**
     * destroy.
     */
    void destroy();


}
