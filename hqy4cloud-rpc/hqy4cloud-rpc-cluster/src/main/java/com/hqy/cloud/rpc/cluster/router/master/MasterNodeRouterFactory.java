package com.hqy.cloud.rpc.cluster.router.master;

import com.hqy.cloud.rpc.cluster.router.Router;
import com.hqy.cloud.rpc.cluster.router.RouterFactory;
import com.hqy.cloud.rpc.model.RpcModel;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/5
 */
public class MasterNodeRouterFactory<T> implements RouterFactory<T> {

    @Override
    public Router<T> createRouter(RpcModel rpcModel) {
        return new MasterNodeRouter<>(rpcModel);
    }
}
