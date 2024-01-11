package com.hqy.cloud.rpc.service;

import com.hqy.cloud.registry.common.Constants;

/**
 * RpcServiceConfig.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/8
 */
public interface RpcServiceConfig {

    /**
     * setting rpc service revision
     * @return default revision {@link Constants#DEFAULT_REVISION}
     */
    default String revision() {
        return Constants.DEFAULT_REVISION;
    }



}
