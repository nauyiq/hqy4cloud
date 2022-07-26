package com.hqy.rpc.registry.nacos.node;

import cn.hutool.core.map.MapUtil;
import com.hqy.base.common.base.lang.ActuatorNodeEnum;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.rpc.common.CommonConstants;
import com.hqy.rpc.common.PubMode;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.common.RPCServerAddress;
import com.hqy.util.JsonUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.hqy.rpc.common.CommonConstants.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/11 15:04
 */
public class Metadata implements Serializable {

    private static final long serialVersionUID = -3757463810919291826L;

    protected int weight;

    protected int pubNode;

    protected final long serverStartTimestamp;

    protected final RPCServerAddress rpcServerAddress;

    protected String hashFactor;

    protected final ActuatorNodeEnum actuatorType;

    public Metadata(int pubNode, RPCServerAddress rpcServerAddress, String hashFactor, ActuatorNodeEnum actuatorType) {
        this(CommonConstants.DEFAULT_WEIGHT, pubNode, rpcServerAddress, hashFactor, actuatorType);
    }

    public Metadata(int weight, int pubNode, RPCServerAddress rpcServerAddress, String hashFactor, ActuatorNodeEnum actuatorType) {
        this.weight = weight;
        this.pubNode = pubNode;
        this.rpcServerAddress = rpcServerAddress;
        this.hashFactor = hashFactor;
        this.actuatorType = actuatorType;
        this.serverStartTimestamp = System.currentTimeMillis();
    }

    public Metadata(RPCModel rpcModel) throws Exception {
        this(rpcModel.getParameters());
    }

    public Metadata(Map<String, String> metadataMap) throws Exception {
        this.weight = Integer.parseInt(metadataMap.getOrDefault(WEIGHT, DEFAULT_WEIGHT + ""));
        this.pubNode = Integer.parseInt(metadataMap.getOrDefault(PUB_MODE, PubMode.GRAY.value + ""));
        this.rpcServerAddress = JsonUtil.toBean(metadataMap.get(RPC_SERVER_ADDR), RPCServerAddress.class);
        this.hashFactor = metadataMap.getOrDefault(HASH_FACTOR, StringConstants.DEFAULT);
        this.serverStartTimestamp = Long.parseLong(metadataMap.getOrDefault(START_SERVER_TIMESTAMP, System.currentTimeMillis() + ""));
        this.actuatorType = ActuatorNodeEnum.valueOf(metadataMap.getOrDefault(ACTUATOR_TYPE, ActuatorNodeEnum.BOTH.name()));
    }

    public Map<String, String> toMetadataMap(){
        return MapUtil.builder(new HashMap<String, String>(6))
                .put(WEIGHT, String.valueOf(getWeight()))
                .put(PUB_MODE, String.valueOf(getPubNode()))
                .put(START_SERVER_TIMESTAMP, String.valueOf(getServerStartTimestamp()))
                .put(HASH_FACTOR, hashFactor)
                .put(RPC_SERVER_ADDR, JsonUtil.toJson(getRpcServerAddress()))
                .put(ACTUATOR_TYPE, actuatorType.name()).build();

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
