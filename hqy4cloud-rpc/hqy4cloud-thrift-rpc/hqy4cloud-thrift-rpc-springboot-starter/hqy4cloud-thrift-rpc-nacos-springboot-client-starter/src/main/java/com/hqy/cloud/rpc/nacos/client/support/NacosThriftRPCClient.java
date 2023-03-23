package com.hqy.cloud.rpc.nacos.client.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.rpc.thrift.ThriftRPCClient;
import com.hqy.cloud.rpc.CommonConstants;
import com.hqy.cloud.rpc.model.RPCServerAddress;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.model.RegistryInfo;
import com.hqy.cloud.rpc.nacos.NacosThriftStarter;
import com.hqy.cloud.rpc.registry.Constants;
import com.hqy.cloud.rpc.registry.api.RegistryFactory;
import com.hqy.cloud.rpc.resgitry.core.NacosRegistryFactory;
import com.hqy.cloud.rpc.resgitry.utils.NacosConfigurationUtils;
import com.hqy.cloud.util.config.ConfigurationContext;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import com.hqy.cloud.util.spring.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.alibaba.nacos.api.annotation.NacosProperties.NAMESPACE;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 16:44
 */
public class NacosThriftRPCClient extends ThriftRPCClient {

    private static final Logger log = LoggerFactory.getLogger(NacosThriftRPCClient.class);

    private NacosThriftRPCClient(RegistryFactory registryFactory) {
        super(registryFactory);
    }

    private static final NacosThriftRPCClient CLIENT = new NacosThriftRPCClient(new NacosRegistryFactory());

    public static NacosThriftRPCClient getInstance() {
        return CLIENT;
    }


    @Override
    protected <T> RPCModel getConsumerRpcModel() {
        RPCModel rpcModel = ProjectContextInfo.getBean(RPCModel.class);
        if (rpcModel != null) {
            return rpcModel;
        }

        try {
            NacosThriftStarter starterServer = SpringContextHolder.getBean(NacosThriftStarter.class);
            rpcModel = starterServer.getRpcModel();
            //register to project context.
            ProjectContextInfo.setBean(rpcModel);
        } catch (Throwable cause) {
            log.warn("Failed get rpc context from spring bean, cause {}", cause.getMessage(), cause);
        }

        if (rpcModel == null) {
            //may be 'main' method or not spring project remoting rpc.
            rpcModel = createDirectRpcModel();
            //register to project context.
            ProjectContextInfo.setBean(rpcModel);
        }
        return rpcModel;
    }

    private  RPCModel createDirectRpcModel() {
        RegistryInfo registryInfo = NacosThriftStarter.buildRegistryInfo(NacosConfigurationUtils.getServerAddress());
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
