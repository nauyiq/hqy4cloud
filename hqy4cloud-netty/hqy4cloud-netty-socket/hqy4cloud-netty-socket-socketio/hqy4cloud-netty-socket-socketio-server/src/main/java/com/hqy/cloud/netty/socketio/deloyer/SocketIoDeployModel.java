package com.hqy.cloud.netty.socketio.deloyer;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.lang.DeployComponent;
import com.hqy.cloud.netty.socketio.SocketIoSocketServer;
import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import com.hqy.cloud.registry.common.model.DeployModel;
import com.hqy.cloud.registry.context.ProjectContext;
import com.hqy.cloud.rpc.cluster.ClusterJoinConstants;
import com.hqy.cloud.socket.model.SocketServerMetadata;
import com.hqy.cloud.common.base.project.ProjectContextInfo;
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

    public SocketIoDeployModel(ProjectInfoModel model, SocketIoSocketServer server) {
        super(model);
        this.server = server;
    }

    @Override
    public void initialize() {
        super.initialize();
        // initialize socketIo server.
        server.initialize();
        ProjectContextInfo contextInfo = ProjectContext.getContextInfo();
        contextInfo.getUip().setSocketPort(server.getInfo().getMetadata().getPort());
    }

    @Override
    public void start() {
        log.info("Start socket.io server by {}, {}.", getModel().getApplicationName(), server.getInfo().getMetadata());
        // start
        server.start();
    }

    @Override
    public Map<String, String> getMetadataMap() {
        Map<String, String> metadataMap = this.server.getInfo().getMetadata().getMetadataMap();
        SocketServerMetadata metadata = server.getMetadata();
        // 判断是否集群启动.
        if (metadata.isCluster()) {
            // 初始化hashFactor
            String hashFactor = getModel().getIp() + StrUtil.COLON + metadata.getPort();
            metadataMap.put(ClusterJoinConstants.HASH_FACTOR,  hashFactor);
        }
        return metadataMap;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onDestroy() {
        this.server.destroy();
    }

    @Override
    public String getModelName() {
        return DeployComponent.SOCKETIO.name;
    }
}
