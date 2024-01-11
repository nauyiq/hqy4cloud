package com.hqy.cloud.netty.socketio.deloyer;

import com.hqy.cloud.common.base.lang.DeployComponent;
import com.hqy.cloud.netty.socketio.SocketIoSocketServer;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.common.model.DeployModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * using support deploy socketIo.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
public class SocketIoDeployModel extends DeployModel {
    private final static Logger log = LoggerFactory.getLogger(SocketIoDeployModel.class);
    private final SocketIoSocketServer server;

    public SocketIoDeployModel(ApplicationModel model, SocketIoSocketServer server) {
        super(model);
        this.server = server;
    }

    @Override
    public void initialize() {
        super.initialize();
        // initialize socketIo server.
        server.initialize();
    }

    @Override
    public void start() {
        log.info("Start socket.io server by {}, {}.", getModel().getApplicationName(), server.getInfo().getMetadata());
        // start
        server.start();
    }

    @Override
    public Map<String, String> getMetadataMap() {
        return this.server.getInfo().getMetadata().getMetadataMap();
    }

    @Override
    public void onDestroy() {
        this.server.destroy();
    }

    @Override
    public String getModelName() {
        return DeployComponent.SOCKET_IO.name;
    }
}
