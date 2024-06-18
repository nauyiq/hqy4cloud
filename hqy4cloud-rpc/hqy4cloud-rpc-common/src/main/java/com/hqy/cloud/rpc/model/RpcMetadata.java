package com.hqy.cloud.rpc.model;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.registry.common.metadata.MetadataService;
import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import com.hqy.cloud.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/9
 */
public class RpcMetadata implements MetadataService {
    public static final String RPC_HASH_FACTOR = "hashFactor";
    public static final String DEFAULT_HASH_FACTOR = StringConstants.DEFAULT;
    public static final String RPC_SERVER_METADATA_ADDRESS = "rpcServerAddress";
    public static final String RPC_SERVICE_INFO = "rpcServiceInfo";

    private String hashFactor;
    private RpcServerAddress rpcServerAddress;
    private List<RpcServiceInfo> rpcServiceInfos;
    private final Map<String, String> metadataMap = new ConcurrentHashMap<>(4);

    public RpcMetadata(String hashFactor) {
        this.hashFactor = hashFactor;
    }

    public RpcMetadata(String hashFactor, RpcServerAddress rpcServerAddress, List<RpcServiceInfo> rpcServiceInfos) {
        this.hashFactor = hashFactor;
        this.rpcServerAddress = rpcServerAddress;
        this.rpcServiceInfos = rpcServiceInfos;
        // init
        metadataMap.put(RPC_HASH_FACTOR, hashFactor);
        metadataMap.put(RPC_SERVER_METADATA_ADDRESS, JsonUtil.toJson(rpcServerAddress));
        metadataMap.put(RPC_SERVICE_INFO, JsonUtil.toJson(rpcServiceInfos));
    }

    public static RpcMetadata of(String hashFactor) {
        return RpcMetadata.of(hashFactor, null, Collections.emptyList());
    }

    public static RpcMetadata of(String hashFactor, RpcServerAddress rpcServerAddress, List<RpcServiceInfo> serviceInfos) {
        return new RpcMetadata(hashFactor, rpcServerAddress, serviceInfos);
    }

    public static RpcMetadata of(ProjectInfoModel model) {
        String hashFactor = model.getParameter(RPC_HASH_FACTOR, DEFAULT_HASH_FACTOR);
        String addressJson = model.getMetadataInfo().getParameter(RPC_SERVER_METADATA_ADDRESS);
        RpcServerAddress serverAddress;
        if (StringUtils.isNotBlank(addressJson)) {
            serverAddress = JsonUtil.toBean(addressJson, RpcServerAddress.class);
        } else {
            serverAddress = RpcServerAddress.of(model.getIp());
        }
        return RpcMetadata.of(hashFactor, serverAddress, Collections.emptyList());
    }

    @Override
    public Map<String, String> getMetadataMap() {
        return metadataMap;
    }


    public String getHashFactor() {
        return hashFactor;
    }

    public void setHashFactor(String hashFactor) {
        this.hashFactor = hashFactor;
        metadataMap.put(RPC_HASH_FACTOR, hashFactor);
    }

    public RpcServerAddress getRpcServerAddress() {
        return rpcServerAddress;
    }

    public void setRpcServerAddress(RpcServerAddress rpcServerAddress) {
        this.rpcServerAddress = rpcServerAddress;
        metadataMap.put(RPC_SERVER_METADATA_ADDRESS, JsonUtil.toJson(rpcServerAddress));
    }

    public List<RpcServiceInfo> getRpcServiceInfos() {
        return rpcServiceInfos;
    }

    public void setRpcServiceInfos(List<RpcServiceInfo> rpcServiceInfos) {
        this.rpcServiceInfos = rpcServiceInfos;
        metadataMap.put(RPC_SERVICE_INFO, JsonUtil.toJson(rpcServiceInfos));
    }
}
