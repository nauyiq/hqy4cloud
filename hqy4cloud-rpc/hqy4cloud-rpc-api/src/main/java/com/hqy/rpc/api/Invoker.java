package com.hqy.rpc.api;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.rpc.api.service.RPCModelService;
import com.hqy.rpc.common.support.RPCModel;

/**
 * Invoker for RPC 'heart'.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 11:14
 */
public interface Invoker<T> extends RPCModelService {

    /**
     * get rpc interface service class type.
     * @return remote rpc interface class type.
     */
    Class<T> getInterface();

    /**
     * reflect method invoke.
     * @param invocation    non-null for {@link Invocation}
     * @return result       method
     * @throws RpcException
     */
    Object invoke(Invocation invocation) throws RpcException;

    /**
     * get client consumer rpc model.
     * @return {@link RPCModel}
     */
    RPCModel getConsumerModel();

}
