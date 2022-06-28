package com.hqy.rpc.registry.api.support;

import com.hqy.rpc.registry.node.Metadata;
import com.hqy.rpc.registry.api.Registry;
import com.hqy.rpc.registry.api.RegistryFactory;
import com.hqy.util.spring.ProjectContextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 16:47
 */
public class AbstractRegistryFactory implements RegistryFactory {

    private static final Logger log = LoggerFactory.getLogger(AbstractRegistryFactory.class);

    private RegistryManager registryManager;

    private ProjectContextInfo projectContextInfo;



    @Override
    public Registry getRegistry(Metadata metadata) {
        return null;
    }
}
