package com.hqy.cloud.socket.api;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.socket.model.SocketServerInfo;
import com.hqy.cloud.socket.model.SocketServerMetadata;

import static com.hqy.cloud.socket.SocketConstants.ENABLED_SOCKET_SERVER_CLUSTER;
import static com.hqy.cloud.socket.SocketConstants.SOCKET_SERVER_CONTEXT;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
public abstract class AbstractSocketServer implements SocketServer {
    private final int bindPort;
    private final ApplicationModel model;
    private String address;
    private SocketServerInfo socketServerInfo;
    protected AbstractSocketServer(ApplicationModel model, int bindPort) {
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

    protected void initialize() {
        String applicationName = model.getApplicationName();
        // 是否集群启动.
        boolean isCluster = model.getParameter(ENABLED_SOCKET_SERVER_CLUSTER, true);
        // 获取context
        String context = model.getParameter(SOCKET_SERVER_CONTEXT, StringConstants.EMPTY);
        SocketServerMetadata metadata = new SocketServerMetadata(bindPort, context, isCluster);

        this.address = model.getIp() + StrUtil.COLON + bindPort;
        this.socketServerInfo = new SocketServerInfo(applicationName, model.getId(), metadata);
    }

    public ApplicationModel getModel() {
        return model;
    }

    protected abstract void onStart();


}
