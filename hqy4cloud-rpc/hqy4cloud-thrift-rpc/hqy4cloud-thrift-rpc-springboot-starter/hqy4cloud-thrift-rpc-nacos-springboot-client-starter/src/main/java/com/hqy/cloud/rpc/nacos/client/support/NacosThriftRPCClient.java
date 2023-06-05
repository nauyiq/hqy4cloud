package com.hqy.cloud.rpc.nacos.client.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.rpc.CommonConstants;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.model.RPCServerAddress;
import com.hqy.cloud.rpc.model.RegistryInfo;
import com.hqy.cloud.rpc.nacos.core.NacosRegistry;
import com.hqy.cloud.rpc.nacos.core.NacosRegistryFactory;
import com.hqy.cloud.rpc.nacos.utils.NacosConfigurationUtils;
import com.hqy.cloud.rpc.registry.api.RegistryFactory;
import com.hqy.cloud.rpc.registry.api.support.RegistryUtil;
import com.hqy.cloud.rpc.thrift.ThriftRPCClient;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.alibaba.nacos.api.annotation.NacosProperties.NAMESPACE;

/**
 * NacosThriftRPCClient.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 16:44
 */
public class NacosThriftRPCClient extends ThriftRPCClient {
    private static final Logger log = LoggerFactory.getLogger(NacosThriftRPCClient.class);

    private NacosThriftRPCClient(RegistryFactory registryFactory, RPCModel rpcModel) {
        super(registryFactory, rpcModel);
    }
    private NacosThriftRPCClient(RegistryFactory registryFactory) {
        super(registryFactory);
    }
    private volatile static NacosThriftRPCClient CLIENT;

    public static NacosThriftRPCClient of(RPCModel rpcModel) {
        synchronized (NacosThriftRPCClient.class) {
            CLIENT = new NacosThriftRPCClient(new NacosRegistryFactory(), rpcModel);
        }
        return CLIENT;
    }

    public static NacosThriftRPCClient getInstance() {
        if (CLIENT == null) {
            synchronized (NacosThriftRPCClient.class) {
                if (CLIENT == null) {
                    // using default consumer model.
                    CLIENT = new NacosThriftRPCClient(new NacosRegistryFactory());
                }
            }
        }
        return CLIENT;
    }

    @Override
    protected <T> RPCModel createConsumerRpcModel() {
        RPCModel rpcModel = ProjectContextInfo.getBean(RPCModel.class);
        if (rpcModel != null) {
            return rpcModel;
        }
        //may be 'main' method or not spring project remoting rpc.
        rpcModel = createDirectRpcModel();
        //register to project context.
        ProjectContextInfo.setBean(rpcModel);
        return rpcModel;
    }

    private RPCModel createDirectRpcModel() {
        RegistryInfo registryInfo = RegistryUtil.buildRegistryInfo(NacosConfigurationUtils.getServerAddress(), NacosRegistry.NAME);
        return new RPCModel(CommonConstants.DIRECT_SERVICE, 0, getGroup(), registryInfo, RPCServerAddress.createConsumerRpcServer(), createdDirectRpcParams());
    }

    private static String getGroup() {
        return NacosConfigurationUtils.getNacosGroup();
    }

    public static Map<String, String> createdDirectRpcParams() {
        Map<String, String> map =  MapUtil.newHashMap();
        map.put(NAMESPACE, "9cd8de3b-030a-49f1-9256-f04de35cdb9e");
        return map;
    }

}
