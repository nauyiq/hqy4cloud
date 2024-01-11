package com.hqy.cloud.rpc;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.service.RpcModelService;

/**
 * RpcStarter.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/4
 */
public interface RpcStarter extends RpcModelService {

    /**
     * start rpc component.
     * @throws RpcException non-catch
     */
    void start() throws RpcException;


}
