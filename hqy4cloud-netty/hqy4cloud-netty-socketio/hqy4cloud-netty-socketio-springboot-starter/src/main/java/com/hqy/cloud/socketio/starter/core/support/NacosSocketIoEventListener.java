package com.hqy.cloud.socketio.starter.core.support;

import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.hqy.cloud.foundation.common.route.SocketClusterStatus;
import com.hqy.cloud.foundation.common.route.SocketClusterStatusManager;
import com.hqy.cloud.rpc.core.Environment;
import com.hqy.cloud.socketio.starter.core.SocketIoServerStarter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 14:30
 */
@Slf4j
@RequiredArgsConstructor
public class NacosSocketIoEventListener implements EventListener {
    private final SocketIoServerStarter starter;

    @Override
    public void onEvent(Event event) {
        if (event instanceof NamingEvent e) {
            //re registry.
            int size = e.getInstances().size();
            SocketClusterStatusManager.registry(new SocketClusterStatus(starter.serviceName(),
                    Environment.getInstance().getEnvironment(), starter.clusterNode(), starter.isCluster(), starter.contextPath()));
            log.info("Do re-registry for nacos event, nodes: {}.", size);
        }
    }
}
