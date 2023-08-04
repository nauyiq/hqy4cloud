package com.hqy.cloud.socketio.starter.core;

import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.corundumstudio.socketio.AuthorizationListener;
import com.hqy.cloud.common.base.config.ConfigConstants;
import com.hqy.cloud.socketio.starter.core.support.DefaultAuthorizationListenerAdaptor;
import com.hqy.cloud.util.config.ConfigurationContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.util.List;

import static com.hqy.cloud.common.base.config.ConfigConstants.*;

/**
 * AbstractSocketIoServerStarter.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/26 10:14
 */
@Slf4j
public abstract class AbstractSocketIoServerStarter implements SocketIoServerStarter {
    private final NacosServiceManager nacosServiceManager;
    private final Environment environment;
    private final String application;
    private volatile String secret;

    public AbstractSocketIoServerStarter(Environment environment, NacosServiceManager nacosServiceManager) {
        this(environment, ConfigurationContext.YamlEnum.APPLICATION_YAML.fineName, nacosServiceManager);
    }

    public AbstractSocketIoServerStarter(Environment environment, String application, NacosServiceManager nacosServiceManager) {
        this.environment = environment;
        this.application = application;
        this.nacosServiceManager = nacosServiceManager;
    }

    @Override
    public String serviceName() {
        return environment.getProperty(APPLICATION_NAME);
    }

    @Override
    public int clusterNode() {
        String serviceName = serviceName();
        try {
            String group = environment.getProperty(NACOS_GROUP, Constants.DEFAULT_GROUP);
            List<Instance> instances = nacosServiceManager.getNamingService().selectInstances(serviceName, group,true);
            return instances.size() + 1;
        } catch (NacosException e) {
            log.warn("Failed execute to abstain nacos instances, service name = {}.", serviceName);
        }
        String property = environment.getProperty(SOCKET_CLUSTER_NODES);
        property = StringUtils.isBlank(property) ? ConfigurationContext.getString(application, SOCKET_CLUSTER_NODES) : property;
        if (StringUtils.isBlank(property)) {
            return DEFAULT_SOCKET_CLUSTER_NODES;
        }
        return Integer.parseInt(property);
    }

    @Override
    public boolean isCluster() {
        String property = environment.getProperty(SOCKET_ENABLE_CLUSTER);
        property = StringUtils.isBlank(property) ? ConfigurationContext.getString(application, SOCKET_ENABLE_CLUSTER) : property;
        if (StringUtils.isBlank(property)) {
            return DEFAULT_SOCKET_ENABLE_CLUSTER;
        }
        return Boolean.parseBoolean(property);
    }

    @Override
    public int clusterHash() {
        String property = environment.getProperty(SOCKET_CLUSTER_HASH);
        property = StringUtils.isBlank(property) ? ConfigurationContext.getString(application, SOCKET_CLUSTER_HASH) : property;
        if (StringUtils.isBlank(property)) {
            return DEFAULT_SOCKET_CLUSTER_NODES;
        }
        return Integer.parseInt(property);
    }

    @Override
    public String authorizationSecret() {
        if (StringUtils.isBlank(secret)) {
            String property = environment.getProperty(SOCKET_AUTHORIZATION_SECRET);
            secret = StringUtils.isBlank(property) ? ConfigurationContext.getString(application, SOCKET_AUTHORIZATION_SECRET) : property;
        }
        return secret;
    }

    @Override
    public AuthorizationListener authorizationListener() {
        return new DefaultAuthorizationListenerAdaptor(authorizationSecret());
    }
}
