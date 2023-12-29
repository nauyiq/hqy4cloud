package com.hqy.cloud.rpc.registry.api;

import com.hqy.cloud.rpc.model.RPCModel;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 16:39
 */
public interface RegistryFactory {

    /**
     * Connect to the registry
     * @param rpcModel  Registry address, is not allowed to be empty
     * @return          Registry reference, never return empty value
     */
    RPCRegistry getRegistry(RPCModel rpcModel);

}
