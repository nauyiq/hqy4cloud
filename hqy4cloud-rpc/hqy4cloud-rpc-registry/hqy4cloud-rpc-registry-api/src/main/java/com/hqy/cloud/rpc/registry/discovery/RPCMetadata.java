package com.hqy.cloud.rpc.registry.discovery;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.rpc.model.PubMode;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.model.RPCServerAddress;
import com.hqy.cloud.util.JsonUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.hqy.cloud.rpc.CommonConstants.*;

/**
 * nacos metadata.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/11 15:04
 */
public class RPCMetadata implements Serializable {

    @Serial
    private static final long serialVersionUID = -3757463810919291826L;

    /**
     * service weight.
     */
    protected int weight;

    /**
     * rpc pub mode, see
     */
    protected int pubNode;

    /**
     * server start timestamp
     */
    protected final long serverStartTimestamp;

    /**
     *  rpc address info.
     */
    protected final RPCServerAddress rpcServerAddress;

    /**
     * hash factor, usually ip
     */
    protected String hashFactor;

    /**
     * rpc actuator type
     */
    protected final ActuatorNode actuatorType;

    /**
     * is master node
     */
    private boolean isMaster;

    /**
     * metadata ex prams
     */
    private Map<String, String> metadataParams;


    public RPCMetadata(int pubNode, RPCServerAddress rpcServerAddress, String hashFactor, ActuatorNode actuatorType) {
        this(DEFAULT_WEIGHT, pubNode, rpcServerAddress, hashFactor, actuatorType, MapUtil.empty());
    }

    public RPCMetadata(int weight, int pubNode, RPCServerAddress rpcServerAddress, String hashFactor, ActuatorNode actuatorType, Map<String, String> metadataParams) {
        this.weight = weight;
        this.pubNode = pubNode;
        this.rpcServerAddress = rpcServerAddress;
        this.hashFactor = hashFactor;
        this.actuatorType = actuatorType;
        this.serverStartTimestamp = System.currentTimeMillis();
        this.metadataParams = metadataParams;
    }

    public RPCMetadata(RPCModel rpcModel) {
        this(rpcModel.getParameters());
    }

    public RPCMetadata(Map<String, String> metadataMap) {
        this.weight = Integer.parseInt(metadataMap.getOrDefault(WEIGHT, DEFAULT_WEIGHT + ""));
        this.pubNode = Integer.parseInt(metadataMap.getOrDefault(PUB_MODE, PubMode.GRAY.value + ""));
        this.rpcServerAddress = JsonUtil.toBean(metadataMap.get(RPC_SERVER_ADDR), RPCServerAddress.class);
        this.hashFactor = metadataMap.getOrDefault(HASH_FACTOR, StringConstants.DEFAULT);
        this.serverStartTimestamp = Long.parseLong(metadataMap.getOrDefault(START_SERVER_TIMESTAMP, System.currentTimeMillis() + ""));
        this.actuatorType = ActuatorNode.valueOf(metadataMap.getOrDefault(ACTUATOR_TYPE, ActuatorNode.BOTH.name()));
        this.isMaster = Boolean.parseBoolean(metadataMap.getOrDefault(MASTER_NODE, Boolean.FALSE.toString()));
    }

    public Map<String, String> toMetadataMap(){
        Map<String, String> map = MapUtil.builder(new HashMap<String, String>(6))
                .put(WEIGHT, String.valueOf(getWeight()))
                .put(PUB_MODE, String.valueOf(getPubNode()))
                .put(START_SERVER_TIMESTAMP, String.valueOf(getServerStartTimestamp()))
                .put(HASH_FACTOR, hashFactor)
                .put(RPC_SERVER_ADDR, JsonUtil.toJson(getRpcServerAddress()))
                .put(ACTUATOR_TYPE, actuatorType.name()).build();
        if (MapUtil.isNotEmpty(metadataParams)) {
            map.putAll(metadataParams);
        }
        return map;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("weight", weight)
                .append("pubNode", pubNode)
                .append("serverStartTimestamp", serverStartTimestamp)
                .append("rpcServerAddress", rpcServerAddress)
                .append("hashFactor", hashFactor)
                .append("actuatorType", actuatorType)
                .toString();
    }

    public Boolean getMaster() {
        return isMaster;
    }

    public void setMaster(Boolean master) {
        isMaster = master;
    }

    public int getPubNode() {
        return pubNode;
    }

    public void setPubNode(int pubNode) {
        this.pubNode = pubNode;
    }

    public long getServerStartTimestamp() {
        return serverStartTimestamp;
    }

    public int getWeight() {
        return weight;
    }

    public RPCServerAddress getRpcServerAddress() {
        return rpcServerAddress;
    }

    public String getHashFactor() {
        return hashFactor;
    }

    public void setHashFactor(String hashFactor) {
        this.hashFactor = hashFactor;
    }
}
