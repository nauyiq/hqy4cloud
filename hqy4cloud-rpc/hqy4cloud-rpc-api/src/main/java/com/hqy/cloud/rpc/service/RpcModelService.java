package com.hqy.cloud.rpc.service;

import com.hqy.cloud.registry.common.context.CloseableService;
import com.hqy.cloud.rpc.model.RpcModel;

/**
 * RpcModelService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23
 */
public interface RpcModelService extends CloseableService {

    /**
     * get rpc model.
     * @return rpc model {@link RpcModel}
     */
    RpcModel getModel();

}
