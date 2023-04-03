package com.hqy.cloud.rpc.resgitry.core;

import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.registry.api.AbstractRegistryFactory;
import com.hqy.cloud.rpc.registry.api.Registry;
import com.hqy.cloud.rpc.resgitry.utils.NacosNamingServiceUtils;

/**
 * NacosRegistry Factory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/5 11:08
 */
public class NacosRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistry(RPCModel rpcModel) {
        return new NacosRegistry(rpcModel, NacosNamingServiceUtils.createNamingService(rpcModel));
    }
}
