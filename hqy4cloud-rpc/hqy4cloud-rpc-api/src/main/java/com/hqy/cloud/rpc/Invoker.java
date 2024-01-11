package com.hqy.cloud.rpc;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.service.RpcModelService;
import com.hqy.cloud.rpc.model.RpcModel;

/**
 * Invoker for RPC 'heart'.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29
 */
public interface Invoker<T> extends RpcModelService {

    /**
     * get rpc interface service class type.
     * @return remote rpc interface class type.
     */
    Class<T> getInterface();

    /**
     * reflect method invoke.
     * @param invocation    non-null for {@link Invocation}
     * @return result       method
     * @throws RpcException e
     */
    Object invoke(Invocation invocation) throws RpcException;


    /**
     * get client consumer rpc model.
     * @return {@link RpcModel}
     */
    RpcModel getConsumerModel();


}
