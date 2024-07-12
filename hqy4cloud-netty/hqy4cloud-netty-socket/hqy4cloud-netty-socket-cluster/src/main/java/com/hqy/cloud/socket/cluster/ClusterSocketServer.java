package com.hqy.cloud.socket.cluster;

import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import com.hqy.cloud.socket.api.AbstractSocketServer;
import com.hqy.cloud.socket.cluster.support.HashSocketCluster;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public abstract class ClusterSocketServer extends AbstractSocketServer {
    private final String routerName;

    protected ClusterSocketServer(ProjectInfoModel model, int bindPort) {
        this(model, bindPort,  HashSocketCluster.NAME);
    }

    protected ClusterSocketServer(ProjectInfoModel model, int bindPort, String router) {
        super(model, bindPort);
        this.routerName = router;
    }

    public String getRouterName() {
        return routerName;
    }



}
