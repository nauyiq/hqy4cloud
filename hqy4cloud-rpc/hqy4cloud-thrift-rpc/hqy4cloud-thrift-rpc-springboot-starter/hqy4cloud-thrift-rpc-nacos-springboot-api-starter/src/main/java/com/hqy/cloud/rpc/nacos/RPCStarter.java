package com.hqy.cloud.rpc.nacos;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.model.RPCModel;

/**
 * Rpc启动器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/14 11:02
 */
public interface RPCStarter {

    /**
     * return rpc model.
     * @return {@link RPCModel}
     */
    RPCModel getRpcModel();

    /**
     * registry project info.
     * @throws RpcException e
     */
    void registerProjectContextInfo() throws RpcException;




}
