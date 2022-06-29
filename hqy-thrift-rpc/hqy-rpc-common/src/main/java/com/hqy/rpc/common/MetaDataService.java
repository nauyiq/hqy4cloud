package com.hqy.rpc.common;

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
