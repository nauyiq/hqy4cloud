package com.hqy.cloud.socket.cluster.client.support;

import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.api.ServiceInstance;
import com.hqy.cloud.registry.api.ServiceNotifyListener;
import com.hqy.cloud.registry.api.support.ApplicationServiceInstance;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.socket.cluster.client.AbstractSocketDirectory;
import com.hqy.cloud.socket.cluster.support.InstanceSocketServer;
import com.hqy.cloud.socket.api.SocketServer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public class DynamicSocketDirectory extends AbstractSocketDirectory implements ServiceNotifyListener {
    private final Registry registry;

    public DynamicSocketDirectory(String socketServerApplication, Registry registry) {
        super(socketServerApplication);
        this.registry = registry;
        notifyAndSubscribed();
    }

    private void notifyAndSubscribed() {
        // 获取socketServer实例列表.
        ApplicationModel model = registry.getModel();
        ApplicationModel lockupModel = ApplicationModel.of(applicationName(), model.getNamespace(), model.getGroup());
        List<ServiceInstance> instances = registry.lookup(lockupModel);
        // 设置socket server.
        notify(instances);
        // 订阅socket服务.
        registry.subscribe(new ApplicationServiceInstance(lockupModel),this);
    }

    @Override
    public synchronized void notify(List<ServiceInstance> instances) {
        List<SocketServer> socketServers = new ArrayList<>(instances.size());
        instances.forEach(instance ->  socketServers.add(new InstanceSocketServer(instance)));
        setSocketServers(socketServers);
    }
}
