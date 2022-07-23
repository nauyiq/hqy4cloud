package com.hqy.rpc.cluster.router.hashfactor;

import com.hqy.rpc.cluster.router.Router;
import com.hqy.rpc.cluster.router.RouterFactory;
import com.hqy.rpc.common.support.RPCModel;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/1 13:45
 */
public class HashFactorRouterFactory<T> implements RouterFactory<T> {

    @Override
    public Router<T> createRouter(RPCModel rpcModel) {
        return new HashFactorRouter<>(rpcModel);
    }
}
