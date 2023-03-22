package com.hqy.cloud.rpc.registry.api;

import com.hqy.cloud.rpc.model.RPCModel;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 13:45
 */
public interface NotifyListener {

    /**
     * Triggered when a service change notification is received.
     * @param rpcModels The list of registered information
     */
    void notify(List<RPCModel> rpcModels);


}
