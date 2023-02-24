package com.hqy.rpc.nacos.starter;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.rpc.common.support.RPCModel;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/14 11:02
 */
public interface RPCStarter {


    RPCModel getRpcModel();

    void registerProjectContextInfo() throws RpcException;




}
