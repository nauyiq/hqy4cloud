package com.hqy.rpc;

import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.rpc.common.MetaDataService;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 11:14
 */
public interface Invoker<T> extends MetaDataService {

    /**
     * get rpc service interface
     * @return service interface
     */
    Class<T> getInterface();

    /**
     * method invoke
     * @param invocation
     * @return result
     * @throws RpcException
     */
    Object invoke(Invocation invocation) throws RpcException;



}
