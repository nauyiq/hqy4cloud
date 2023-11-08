package com.hqy.cloud.rpc.nacos.core;

import com.hqy.cloud.rpc.config.deploy.AbstractRPCStarter;
import com.hqy.cloud.rpc.model.ApplicationModel;
import com.hqy.cloud.rpc.nacos.node.Metadata;
import com.hqy.cloud.rpc.nacos.node.NacosServerInfo;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * NacosRPCStarter.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 17:44
 */
public class NacosRPCStarter extends AbstractRPCStarter implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(NacosRPCStarter.class);

    private final NacosServerInfo nacosServerInfo;
    private final Metadata metadata;

    public NacosRPCStarter(ApplicationModel model, NacosServerInfo nacosServerInfo,
                           Metadata metadata) {
        super(model);
        this.nacosServerInfo = nacosServerInfo;
        this.metadata = metadata;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public NacosServerInfo getNacosServerInfo() {
        return nacosServerInfo;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ProjectContextInfo.setBean(NacosServerInfo.class, nacosServerInfo);
    }
}
