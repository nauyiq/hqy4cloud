package com.hqy.cloud.socket.api;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import com.hqy.cloud.socket.model.SocketServerInfo;
import com.hqy.cloud.socket.model.SocketServerMetadata;

import static com.hqy.cloud.socket.SocketConstants.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
public abstract class AbstractSocketServer implements SocketServer {
    private final int bindPort;
    private final ProjectInfoModel model;
    private String address;
    private SocketServerInfo socketServerInfo;
    protected AbstractSocketServer(ProjectInfoModel model, int bindPort) {
        this.model = model;
        this.bindPort = bindPort;
    }

    @Override
    public void start() {
        onStart();
    }

    @Override
    public SocketServerInfo getInfo() {
        return socketServerInfo;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public SocketServerMetadata getMetadata() {
        return getInfo().getMetadata();
    }

    protected void initialize() {
        String applicationName = model.getApplicationName();
        // 是否集群启动.
        boolean isCluster = model.getParameter(ENABLED_SOCKET_SERVER_CLUSTER, true);
        // 获取context
        String context = model.getParameter(SOCKET_SERVER_CONTEXT, StringConstants.EMPTY);
        // 获取cluster 类型
        String clusterType = model.getParameter(SOCKET_CLUSTER_TYPE);
        SocketServerMetadata metadata = new SocketServerMetadata(bindPort, clusterType, context, isCluster);

        this.address = model.getIp() + StrUtil.COLON + bindPort;
        this.socketServerInfo = new SocketServerInfo(applicationName, model.getId(), metadata);
    }

    public ProjectInfoModel getModel() {
        return model;
    }

    protected abstract void onStart();


}
