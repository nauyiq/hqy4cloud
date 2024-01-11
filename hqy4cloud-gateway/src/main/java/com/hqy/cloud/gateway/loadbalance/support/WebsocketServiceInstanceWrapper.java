package com.hqy.cloud.gateway.loadbalance.support;

import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
public class WebsocketServiceInstanceWrapper implements ServiceInstance {
    private final int socketPort;
    private final ServiceInstance serviceInstance;

    public WebsocketServiceInstanceWrapper(int socketPort, ServiceInstance serviceInstance) {
        this.socketPort = socketPort;
        this.serviceInstance = serviceInstance;
    }

    @Override
    public String getInstanceId() {
        return serviceInstance.getInstanceId();
    }

    @Override
    public String getServiceId() {
        return serviceInstance.getServiceId();
    }

    @Override
    public String getHost() {
        return serviceInstance.getHost();
    }

    @Override
    public int getPort() {
        return socketPort;
    }

    @Override
    public boolean isSecure() {
        return serviceInstance.isSecure();
    }

    @Override
    public URI getUri() {
        return serviceInstance.getUri();
    }

    @Override
    public Map<String, String> getMetadata() {
        return serviceInstance.getMetadata();
    }

    @Override
    public String getScheme() {
        return serviceInstance.getScheme();
    }
}
