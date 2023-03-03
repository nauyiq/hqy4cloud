package com.hqy.rpc.nacos.client.starter.support;

import com.hqy.rpc.client.thrift.ThriftRPCClient;
import com.hqy.rpc.common.CommonConstants;
import com.hqy.rpc.common.RPCServerAddress;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.common.support.RegistryInfo;
import com.hqy.rpc.nacos.starter.NacosThriftStarter;
import com.hqy.rpc.registry.Constants;
import com.hqy.rpc.registry.api.RegistryFactory;
import com.hqy.rpc.registry.nacos.NacosRegistryFactory;
import com.hqy.rpc.registry.nacos.util.NacosConfigurationUtils;
import com.hqy.cloud.util.config.ConfigurationContext;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import com.hqy.cloud.util.spring.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        return new RPCModel(CommonConstants.DIRECT_SERVICE, 0, getGroup(), registryInfo, RPCServerAddress.createConsumerRpcServer());
    }

    private static String getGroup() {
        String group = ConfigurationContext.getString(ConfigurationContext.YamlEnum.BOOTSTRAP_DEV_YAML, Constants.DEV_REGISTRY_GROUP_KEY);
        return StringUtils.isBlank(group) ? CommonConstants.DEFAULT_GROUP : group;
    }

}
