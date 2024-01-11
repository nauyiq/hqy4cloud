package com.hqy.cloud.rpc.starter.model;

import com.hqy.cloud.common.base.lang.DeployComponent;
import com.hqy.cloud.rpc.model.RpcModel;
import com.hqy.cloud.rpc.starter.client.Client;

/**
 * rpc consumer deploy model, help to rpc consumer get started.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/4
 */
public class RpcConsumerDeployModel extends RpcDeployModel {
    private final Client client;

    public RpcConsumerDeployModel(RpcModel rpcModel, Client client) {
        super(rpcModel, client);
        this.client = client;
    }

    @Override
    protected void doInit() {
        client.initialize();
    }

    @Override
    public void onDestroy() {
        client.destroy();
    }

    @Override
    public String getModelName() {
        return DeployComponent.RPC_CLIENT.name;
    }

}
