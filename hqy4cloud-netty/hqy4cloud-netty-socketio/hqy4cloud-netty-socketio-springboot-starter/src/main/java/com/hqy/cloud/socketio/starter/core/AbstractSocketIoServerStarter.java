package com.hqy.cloud.socketio.starter.core;

import com.alibaba.cloud.nacos.NacosServiceManager;
import com.corundumstudio.socketio.AuthorizationListener;
import com.hqy.cloud.socketio.starter.core.support.DefaultAuthorizationListenerAdaptor;
import com.hqy.cloud.util.config.ConfigurationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import static com.hqy.cloud.common.base.config.ConfigConstants.*;

/**
 * AbstractSocketIoServerStarter.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/26 10:14
 */
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
