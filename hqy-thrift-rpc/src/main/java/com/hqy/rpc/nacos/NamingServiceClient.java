package com.hqy.rpc.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.pojo.Cluster;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.fundation.common.base.lang.BaseStringConstants;
import com.hqy.fundation.common.base.project.MicroServiceHelper;
import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.rpc.thrift.ex.ThriftRpcHelper;
import com.hqy.util.AssertUtil;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * NamingService nacos客户端工具类，
 * 当前服务注册进nacos 会初始化此类.
 *
 * https://nacos.io/zh-cn/docs/sdk.html SDK地址...<br>
 * nacos的客户端心跳机制是开启了线程定时发送心跳信息给服务端。<br>
 * nacos的服务端心跳机制也是开启线程定时监听客户端的各种状态从而判断是否健康。<br>
 * nacos用了service，clusterMap，instances来维护服务与它的实例们的关系。<br>
 * https://blog.csdn.net/qq_40634846/article/details/111589989<br>
 *
 * Nacos两种健康检查模式: 1 agent上报模式  2 服务端主动检测 <br>
 * 1 客户端通过心跳上报方式告知服务端(nacos注册中心)健康状态；默认心跳间隔5秒；nacos会在超过15秒未收到心跳后将实例设置为不健康状态；超过30秒将实例删除；<br>
 * 2 服务端健康检查。nacos主动探知客户端健康状态，默认间隔为20秒；健康检查失败后实例会被标记为不健康，不会被立即删除。 <br><br>
 * 临时实例通过agent上报模式实现健康检查。nacos 顾客端注册的节点，默认是临时实例。 <br>
 * 临时和持久化的区别主要在健康检查失败后的表现，持久化实例健康检查失败后会被标记成不健康，而临时实例会直接从列表中被删除。<br>
 *
 * service，clusterMap，instances:一个服务对应一个Service，一个服务可以有多个集群，一个集群可以有多个实例(持久化节点集合和临时节点集合)<br><br>
 * 因为Nacos维护的本地注册表就是Map<String,Cluster>， 其中Cluster中存在Set<Instance>
 * 因此NamingServiceClient获取的NamingService是当前实例连接的nacos服务
 *
 * Nacos 2.0架构升级成为长连接模型 通讯层通过Grpc和RSocket实现长连接数据传输和推送能力
 *
 * @author qiyuan.hong
 * @date  2021-09-18 11:28
 */
@Slf4j
public class NamingServiceClient {

    private static NamingService namingService = null;

    private static final NamingServiceClient INSTANCE = new NamingServiceClient();

    /**
     * 关闭状态标记变量
     */
    private static boolean close = false;

    private NamingServiceClient() {}

    public static NamingServiceClient getInstance() {return INSTANCE;}


    /**
     * 获取NamingService对象
     * @return NamingService
     */
    public static NamingService getNamingService() {
        if (namingService == null && !close) {
            synchronized (NamingServiceClient.class) {
                if (namingService == null) {
                    namingService = buildNamingService();
                }
            }
        }
        return namingService;
    }

    /**
     * 客户端是否关闭
     * @return
     */
    public boolean status() {
        return !close;
    }

    /**
     * 将状态标记为close
     */
    public void close() {
        close = true;
        try {
            namingService.shutDown();
            namingService = null;
        } catch (NacosException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取NamingService
     * @return NamingService
     */
    private static NamingService buildNamingService() {
        if (CommonSwitcher.ENABLE_SPRING_CONTEXT.isOff()) {
            ProjectContextInfo info = SpringContextHolder.getProjectContextInfo();
            Map<String, Object> attributes = info.getAttributes();
            namingService = (NamingService) attributes.get(BaseStringConstants.NACOS_NAMING_SERVICE);
        } else {
            NacosDiscoveryProperties nacosDiscoveryProperties = SpringContextHolder.getBean(NacosDiscoveryProperties.class);
            namingService = nacosDiscoveryProperties.namingServiceInstance();
        }

        AssertUtil.notNull(namingService, "Get NamingService failure, check service registration result.");

        return namingService;
    }


    /**
     * 加载所有服务在远程服务nacos中的实例
     * @param healthy true表示健康的实例
     * @return
     */
    public List<ClusterNode> loadAllProjectNodeInfo(boolean healthy) {
        //加载所有服务在远程服务nacos中的实例
        List<ClusterNode> nodes = new ArrayList<>();
        Set<String> serviceEnNames = MicroServiceHelper.getServiceEnNames();
        for (String serviceEnName : serviceEnNames) {
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.info("@@@ LoadAllProjectNodeInfo, serviceName -> {}", serviceEnName);
            }
            try {
                List<Instance> instances = NamingServiceClient.getInstance().selectInstances(serviceEnName, healthy);
                if (CollectionUtils.isEmpty(instances)) {
                    log.info("{} server instance is empty.", serviceEnName);
                    continue;
                }
                for (Instance instance : instances) {
                    nodes.add(ThriftRpcHelper.copy(instance));
                }
            } catch (Exception e) {
                log.error("@@@ loadAllProjectNodeInfo error, {}, {}", e.getClass().getName(), e.getMessage());
            }
        }
        return nodes;
    }

    public List<ClusterNode> loadProjectNodeInfo(String serviceName, boolean healthy) {
        List<ClusterNode> nodes = new ArrayList<>();
        //根据服务名获取远程服务nacos中的实例
        try {
            List<Instance> instances = NamingServiceClient.getNamingService().selectInstances(serviceName, healthy);
            if (CollectionUtils.isEmpty(instances)) {
                log.warn("@@@ The current [{}] instance list is empty, please checking node status.", serviceName);
            } else {
                nodes = instances.stream().map(ThriftRpcHelper::copy).collect(Collectors.toList());
            }

        } catch (Exception e) {
            log.error("@@@ loadProjectNodeInfo error, {}, {}", e.getClass().getName(), e.getMessage());
        }
        return nodes;
    }



    /**
     * 注册一个实例到服务。一个服务对应一个Service，一个服务可以有多个集群，一个集群可以有多个实例
     * @param serviceName
     * @param groupName
     * @param ip
     * @param port
     * @throws NacosException
     */
    public void registerInstance(String serviceName, String groupName, String ip, int port) throws NacosException {
        getNamingService().registerInstance(serviceName, groupName, ip, port);
    }


    /**
     * 注册一个实例到服务。一个服务对应一个Service，一个服务可以有多个集群，一个集群可以有多个实例
     * @param serviceName
     * @param groupName
     * @param instance
     * @throws NacosException
     */
    public void registerInstance(String serviceName, String groupName, Instance instance) throws NacosException{
        getNamingService().registerInstance(serviceName, groupName, instance);
    }


    /**
     * 删除服务下的一个实例。
     * @param serviceName
     * @param ip
     * @param port
     * @throws NacosException
     */
    void deregisterInstance(String serviceName, String ip, int port) throws NacosException{
        getNamingService().deregisterInstance(serviceName, ip, port);
    }


    /**
     ** 获取服务下的所有实例。
     * @param serviceName
     * @return
     * @throws NacosException
     */
    List<Instance> getAllInstances(String serviceName) throws NacosException{
        return getNamingService().getAllInstances(serviceName);
    }


    /**
     ** 获取健康或不健康实例列表
     * @param serviceName
     * @param healthy
     * @return 根据条件获取过滤后的实例列表。
     * @throws NacosException
     */
    List<Instance> selectInstances(String serviceName, boolean healthy) throws NacosException{
        return getNamingService().selectInstances(serviceName, healthy);
    }


    /**
     ** 根据负载均衡算法随机获取一个健康实例。
     * @param serviceName
     * @return
     * @throws NacosException
     */
    Instance selectOneHealthyInstance(String serviceName) throws NacosException{
        return getNamingService().selectOneHealthyInstance(serviceName);
    }


    /**
     ** 监听服务下的实例列表变化。
     * @param serviceName
     * @param listener
     * @throws NacosException
     */
    void subscribe(String serviceName, EventListener listener) throws NacosException{
        getNamingService().subscribe(serviceName, listener);
    }


    /**
     ** 取消监听服务下的实例列表变化。
     * @param serviceName
     * @param listener
     * @throws NacosException
     */
    void unsubscribe(String serviceName, EventListener listener) throws NacosException{
        getNamingService().unsubscribe(serviceName, listener);
    }

}
