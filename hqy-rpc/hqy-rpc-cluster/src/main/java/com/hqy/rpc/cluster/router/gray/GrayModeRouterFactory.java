package com.hqy.rpc.cluster.router.gray;

import com.hqy.rpc.cluster.router.Router;
import com.hqy.rpc.cluster.router.RouterFactory;
import com.hqy.rpc.common.support.RPCModel;

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
