package com.hqy.cloud.rpc.cluster.router.hashfactor;

import com.hqy.cloud.rpc.cluster.router.Router;
import com.hqy.cloud.rpc.cluster.router.RouterFactory;
import com.hqy.cloud.rpc.model.RpcModel;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/1 13:45
 */
public class HashFactorRouterFactory<T> implements RouterFactory<T> {

    @Override
    public Router<T> createRouter(RpcModel rpcModel) {
        return new HashFactorRouter<>(rpcModel);
    }
}
