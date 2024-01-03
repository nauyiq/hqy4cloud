package com.hqy.cloud.registry.nacos.naming;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;

import java.util.List;

/**
 * NamingService nacos服务包装类，
 * 当前服务注册进nacos 会初始化此类.
 * <p>
 * https://nacos.io/zh-cn/docs/sdk.html SDK地址...<br>
 * nacos的客户端心跳机制是开启了线程定时发送心跳信息给服务端。<br>
 * nacos的服务端心跳机制也是开启线程定时监听客户端的各种状态从而判断是否健康。<br>
 * nacos用了service，clusterMap，instances来维护服务与它的实例们的关系。<br>
 * https://blog.csdn.net/qq_40634846/article/details/111589989<br>
 * <p>
 * Nacos两种健康检查模式: 1 agent上报模式  2 服务端主动检测 <br>
 * 1 客户端通过心跳上报方式告知服务端(nacos注册中心)健康状态；默认心跳间隔5秒；nacos会在超过15秒未收到心跳后将实例设置为不健康状态；超过30秒将实例删除；<br>
 * 2 服务端健康检查。nacos主动探知客户端健康状态，默认间隔为20秒；健康检查失败后实例会被标记为不健康，不会被立即删除。 <br><br>
 * 临时实例通过agent上报模式实现健康检查。nacos 顾客端注册的节点，默认是临时实例。 <br>
 * 临时和持久化的区别主要在健康检查失败后的表现，持久化实例健康检查失败后会被标记成不健康，而临时实例会直接从列表中被删除。<br>
 * <p>
 * service，clusterMap，instances:一个服务对应一个Service，一个服务可以有多个集群，一个集群可以有多个实例(持久化节点集合和临时节点集合)<br><br>
 * 因为Nacos维护的本地注册表就是Map<String,Cluster>， 其中Cluster中存在Set<Instance>
 * 因此NamingServiceClient获取的NamingService是当前实例连接的nacos服务
 * <p>
 * Nacos 2.0架构升级成为长连接模型 通讯层通过Grpc和RSocket实现长连接数据传输和推送能力
 *
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public record NamingServiceWrapper(NamingService namingService,
                                   NamingMaintainService namingMaintainService,
                                   String defaultGroup) {

    public String getServerStatus() {
        return namingService.getServerStatus();
    }

    public void subscribe(String serviceName, EventListener eventListener) throws NacosException {
        namingService.subscribe(serviceName, defaultGroup, eventListener);
    }

    public void subscribe(String serviceName, String group, EventListener eventListener) throws NacosException {
        namingService.subscribe(serviceName, group, eventListener);
    }

    public void unsubscribe(String serviceName, EventListener listener) throws NacosException {
        namingService.unsubscribe(serviceName, defaultGroup, listener);
    }

    public void unsubscribe(String serviceName, String group, EventListener listener) throws NacosException {
        namingService.unsubscribe(serviceName, group, listener);
    }

    public void updateInstance(String serviceName, Instance instance) throws NacosException {
        namingMaintainService.updateInstance(serviceName, defaultGroup, instance);
    }

    public void updateInstance(String serviceName, String group, Instance instance) throws NacosException {
        namingMaintainService.updateInstance(serviceName, group, instance);
    }

    public List<Instance> getAllInstances(String serviceName, String group) throws NacosException {
        return namingService.getAllInstances(serviceName, group);
    }

    public void registerInstance(String serviceName, String group, Instance instance) throws NacosException {
        namingService.registerInstance(serviceName, group, instance);
    }

    public void deregisterInstance(String serviceName, String group, String ip, int port) throws NacosException {
        namingService.deregisterInstance(serviceName, group, ip, port);
    }


    public void deregisterInstance(String serviceName, String group, Instance instance) throws NacosException {
        namingService.deregisterInstance(serviceName, group, instance);
    }

    public ListView<String> getServicesOfServer(int pageNo, int pageSize, String parameter) throws NacosException {
        return namingService.getServicesOfServer(pageNo, pageSize, parameter);
    }

    public List<Instance> selectInstances(String serviceName, boolean healthy) throws NacosException {
        return namingService.selectInstances(serviceName, defaultGroup, healthy);
    }

    public List<Instance> selectInstances(String serviceName, String group, boolean healthy) throws NacosException {
        return namingService.selectInstances(serviceName, group, healthy);
    }

}
