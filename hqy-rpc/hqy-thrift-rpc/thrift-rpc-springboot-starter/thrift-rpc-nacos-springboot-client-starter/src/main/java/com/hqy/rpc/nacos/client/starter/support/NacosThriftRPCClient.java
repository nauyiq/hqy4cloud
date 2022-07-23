package com.hqy.rpc.nacos.client.starter.support;

import com.hqy.rpc.client.thrift.ThriftRPCClient;
import com.hqy.rpc.common.CommonConstants;
import com.hqy.rpc.common.RPCServerAddress;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.common.support.RegistryInfo;
import com.hqy.rpc.nacos.starter.NacosThriftStarter;
import com.hqy.rpc.registry.api.RegistryFactory;
import com.hqy.rpc.registry.nacos.NacosRegistryFactory;
import com.hqy.rpc.registry.nacos.util.NacosConfigurationUtils;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
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

    public static RPCModel createDirectRpcModel() {
        RegistryInfo registryInfo = NacosThriftStarter.buildRegistryInfo(NacosConfigurationUtils.getServerAddress());
        RPCModel rpcModel = new RPCModel(CommonConstants.DIRECT_SERVICE, 0, registryInfo, RPCServerAddress.createConsumerRpcServer());
        ProjectContextInfo.setBean(rpcModel);
        return rpcModel;
    }

}
