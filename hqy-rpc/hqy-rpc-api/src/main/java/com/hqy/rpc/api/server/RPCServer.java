package com.hqy.rpc.api.server;

import com.hqy.rpc.api.service.RPCService;
import com.hqy.rpc.common.RPCServerAddress;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/7 17:05
 */
public interface RPCServer {

    /**
     * return rpc server address.
     * @return {@link RPCServerAddress}
     */
    RPCServerAddress getServerAddr();

    /**
     * Register RPC interfaces to toe registry.
     * @return {@link RPCService}
     */
    List<RPCService> getRegistryRpcServices();

    /**
     * this rpc server already destroy?
     * @return destroy?
     */
    boolean isDestroy();

    /**
     * destroy rpc server.
     */
    void destroy();

}
