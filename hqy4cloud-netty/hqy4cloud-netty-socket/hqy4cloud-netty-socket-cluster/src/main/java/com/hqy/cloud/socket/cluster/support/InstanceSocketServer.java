package com.hqy.cloud.socket.cluster.support;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.registry.api.ServiceInstance;
import com.hqy.cloud.registry.common.metadata.MetadataInfo;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.socket.SocketConstants;
import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.model.SocketServerInfo;
import com.hqy.cloud.socket.model.SocketServerMetadata;
import com.hqy.foundation.authorization.AuthorizationService;

/**
 * 客户端视角调用获取的到的Socket Instance.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public class InstanceSocketServer implements SocketServer {
    private final ServiceInstance serviceInstance;
    private final SocketServerInfo info;
    private final String address;

    public InstanceSocketServer(ServiceInstance serviceInstance) {
        AssertUtil.notNull(serviceInstance, "Socket instance should not be null.");
        this.serviceInstance = serviceInstance;
        ApplicationModel model = serviceInstance.getApplicationModel();
        MetadataInfo metadataInfo = model.getMetadataInfo();
        // 获取socket服务的元数据.
        String socketMetadata = metadataInfo.getParameter(SocketConstants.SOCKET_SERVER_DEPLOY_METADATA_KEY);
        SocketServerMetadata metadata = JsonUtil.toBean(socketMetadata, SocketServerMetadata.class);
        this.info = new SocketServerInfo(model.getApplicationName(), model.getId(), metadata);
        this.address = model.getIp() + StrUtil.COLON + metadata.getPort();
    }


    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public SocketServerInfo getInfo() {
        return info;
    }

    @Override
    public SocketServerMetadata getMetadata() {
        return getInfo().getMetadata();
    }

    @Override
    public AuthorizationService getAuthorizationService() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAvailable() {
        return serviceInstance.isHealthy();
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException();
    }
}
