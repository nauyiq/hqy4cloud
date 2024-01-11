package com.hqy.cloud.rpc.starter.server;

import com.hqy.cloud.rpc.RpcStarter;
import com.hqy.cloud.rpc.model.RpcServerAddress;
import com.hqy.cloud.rpc.service.RPCService;

import java.util.List;

/**
 * RpcServer.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/7
 */
public interface RpcServer extends RpcStarter {

    /**
     * return rpc server address.
     * @return {@link RpcServerAddress}
     */
    RpcServerAddress getServerAddr();

    /**
     * Register RPC interfaces to toe registry.
     * @return {@link RPCService}
     */
    List<RPCService> getRegistryRpcServices();

    /**
     * init rpc client.
     */
    void initialize();

    /**
     * this rpc server already destroy?
     * @return destroy?
     */
    boolean isDestroy();


}
