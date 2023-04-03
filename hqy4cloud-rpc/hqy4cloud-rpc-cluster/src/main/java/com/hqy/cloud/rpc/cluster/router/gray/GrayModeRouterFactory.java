package com.hqy.cloud.rpc.cluster.router.gray;

import com.hqy.cloud.rpc.cluster.router.Router;
import com.hqy.cloud.rpc.cluster.router.RouterFactory;
import com.hqy.cloud.rpc.model.RPCModel;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/1 11:00
 */
public class GrayModeRouterFactory<T> implements RouterFactory<T> {

    @Override
    public Router<T> createRouter(RPCModel rpcModel) {
        return new GrayModeRouter<>(rpcModel);
    }
}
