package com.hqy.rpc.registry.nacos;

import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.registry.api.AbstractRegistryFactory;
import com.hqy.rpc.registry.api.Registry;
import com.hqy.rpc.registry.nacos.util.NacosNamingServiceUtils;

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
