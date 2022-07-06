package com.hqy.rpc.common;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 16:30
 */
public interface MetaDataService extends CloseableService{

    /**
     * get metadata
     * @return Metadata
     */
    Metadata getMetadata();

}
