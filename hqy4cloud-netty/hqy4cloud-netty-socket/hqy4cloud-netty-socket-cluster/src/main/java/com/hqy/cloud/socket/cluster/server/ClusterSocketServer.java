package com.hqy.cloud.socket.cluster.server;

import com.hqy.cloud.registry.common.context.BeanRepository;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.socket.cluster.router.ClusterRouters;
import com.hqy.cloud.socket.cluster.router.SocketRouter;
import com.hqy.cloud.socket.cluster.router.id.IdSocketRouter;
import com.hqy.cloud.socket.api.AbstractSocketServer;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public abstract class ClusterSocketServer extends AbstractSocketServer {
    private final SocketRouter router;

    protected ClusterSocketServer(ApplicationModel model, int bindPort) {
        this(model, bindPort,  BeanRepository.getInstance().getBean(IdSocketRouter.class));
    }

    protected ClusterSocketServer(ApplicationModel model, int bindPort, SocketRouter router) {
        super(model, bindPort);
        this.router = router;
    }

    @Override
    protected void onStart() {
        ClusterRouters.registerRouter(getInfo().getApplicationName(), router);
    }
}
