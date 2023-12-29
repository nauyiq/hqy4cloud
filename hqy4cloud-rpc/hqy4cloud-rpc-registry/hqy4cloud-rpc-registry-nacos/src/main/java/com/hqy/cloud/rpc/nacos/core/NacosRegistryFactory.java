package com.hqy.cloud.rpc.nacos.core;

import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.registry.api.AbstractRegistryFactory;
import com.hqy.cloud.rpc.registry.api.RPCRegistry;
import com.hqy.cloud.rpc.nacos.utils.NacosNamingServiceUtils;

/**
 * NacosRegistry Factory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/5 11:08
 */
public class NacosRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected RPCRegistry createRegistry(RPCModel rpcModel) {
        return new NacosRPCRegistry(rpcModel, NacosNamingServiceUtils.createNamingService(rpcModel));
    }
}
