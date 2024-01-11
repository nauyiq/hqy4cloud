package com.hqy.cloud.rpc.starter.model;

import com.hqy.cloud.rpc.model.RpcMetadata;
import com.hqy.cloud.rpc.model.RpcModel;
import com.hqy.cloud.rpc.model.RpcServiceInfo;
import com.hqy.cloud.rpc.service.RPCService;
import com.hqy.cloud.rpc.starter.server.RpcServer;
import com.hqy.cloud.util.JsonUtil;

import java.util.List;
import java.util.Map;

/**
 * rpc provider deploy model, help to rpc provider get started.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/4
 */
public class RpcProviderDeployModel extends RpcDeployModel {
    private static final String NAME = RpcProviderDeployModel.class.getSimpleName();
    private final RpcServer rpcServer;

    public RpcProviderDeployModel(RpcModel rpcModel, RpcServer rpcServer) {
        super(rpcModel, rpcServer);
        this.rpcServer = rpcServer;
    }

    @Override
    protected void doInit() {
        rpcServer.initialize();
    }

    @Override
    public void onDestroy() {
        rpcServer.destroy();
    }

    @Override
    public String getModelName() {
        return NAME;
    }

    @Override
    public Map<String, String> getMetadataMap() {
        RpcMetadata metadata = getRpcModel().getMetadata();
        return metadata.getMetadataMap();
    }

    private String buildRpcServiceInfo() {
        List<RPCService> registryRpcServices = this.rpcServer.getRegistryRpcServices();
        List<RpcServiceInfo> rpcServiceInfos = registryRpcServices.stream().map(service -> new RpcServiceInfo(service.getClass().getName(), service.revision())).toList();
        return JsonUtil.toJson(rpcServiceInfos);
    }



}
