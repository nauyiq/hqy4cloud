package com.hqy.rpc.cluster.router.hashfactor;

import com.hqy.rpc.cluster.router.Router;
import com.hqy.rpc.cluster.router.RouterFactory;
import com.hqy.rpc.common.Metadata;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/1 13:45
 */
public class HashFactorRouterFactory implements RouterFactory {

    @Override
    public Router createRouter(Metadata metadata) {
        return new HashFactorRouter(metadata);
    }
}
