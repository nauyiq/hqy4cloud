package com.hqy.cloud.rpc.cluster.router;

import com.hqy.cloud.rpc.model.RPCModel;

/**
 * RouterFactory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/30 17:34
 */
public interface RouterFactory<T> {

    /**
     * create router.
     * @param rpcModel {@link RPCModel}
     * @return           {@link Router}
     */
    Router<T> createRouter(RPCModel rpcModel);

}
