package com.hqy.rpc.api.service;

import com.hqy.rpc.common.CloseableService;
import com.hqy.rpc.common.support.RPCModel;

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
