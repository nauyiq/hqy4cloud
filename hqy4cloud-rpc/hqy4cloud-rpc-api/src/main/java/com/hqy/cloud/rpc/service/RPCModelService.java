package com.hqy.cloud.rpc.service;

import com.hqy.cloud.rpc.CloseableService;
import com.hqy.cloud.rpc.model.RPCModel;

/**
 * RPCModelService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 16:30
 */
public interface RPCModelService extends CloseableService {

    /**
     * get rpc model.
     * @return rpc model {@link RPCModel}
     */
    RPCModel getModel();

}
