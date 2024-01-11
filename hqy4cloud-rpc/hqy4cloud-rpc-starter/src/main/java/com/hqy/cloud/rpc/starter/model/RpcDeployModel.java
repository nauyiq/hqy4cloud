package com.hqy.cloud.rpc.starter.model;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.registry.common.model.DeployModel;
import com.hqy.cloud.rpc.RpcStarter;
import com.hqy.cloud.rpc.model.RpcMetadata;
import com.hqy.cloud.rpc.model.RpcModel;

import java.util.Map;

/**
 * RpcDeployerModel.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/4
 */
public abstract class RpcDeployModel extends DeployModel {
    private final RpcStarter starter;
    private final RpcModel rpcModel;

    public RpcDeployModel(RpcModel rpcModel, RpcStarter starter) {
        super(rpcModel.getModel());
        this.starter = starter;
        this.rpcModel = rpcModel;
    }

    @Override
    public void initialize() {
        super.initialize();

        // do init.
        doInit();
    }

    @Override
    public void start() {
        // start starter.
        if (starter.isAvailable()) {
            starter.start();
        } else {
            throw new RpcException("RpcStarter start failed, starter not available.");
        }
    }

    /**
     * do initialize.
     */
    protected abstract void doInit();

    @Override
    public Map<String, String> getMetadataMap() {
        RpcMetadata metadata = getRpcModel().getMetadata();
        return metadata.getMetadataMap();
    }

    public RpcModel getRpcModel() {
        return rpcModel;
    }
}
