package com.hqy.rpc.registry.nacos;

import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.registry.api.AbstractRegistryFactory;
import com.hqy.rpc.registry.api.Registry;
import com.hqy.rpc.registry.api.RegistryFactory;
import com.hqy.rpc.registry.nacos.util.NacosNamingServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NacosRegistry Factory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/5 11:08
 */
public class NacosRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistry(Metadata metadata) {
        return new NacosRegistry(metadata, NacosNamingServiceUtils.createNamingService(metadata));
    }
}
