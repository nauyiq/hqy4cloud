package com.hqy.rpc.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.config.impl.ServerlistChangeEvent;
import com.alibaba.nacos.client.naming.event.InstancesChangeEvent;
import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.NotifyCenter;
import com.alibaba.nacos.common.notify.listener.Subscriber;
import com.hqy.fundation.common.base.lang.BaseStringConstants;
import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.rpc.nacos.listener.NodeActivityListener;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.rpc.regist.UsingIpPort;
import com.hqy.util.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author qy
 * @date  2021-09-17 17:22
 */
public abstract class AbstractNacosClient implements NacosClient {

    private static final Logger log = LoggerFactory.getLogger(AbstractNacosClient.class);


    protected final List<NodeActivityListener> observers = new ArrayList<>();

    /**
     * 活着的节点一览表
     */
    protected static List<ClusterNode> allNodes = new CopyOnWriteArrayList<>();

    /**
     * 活着的节点一览表 ，根据灰白模式 缓存起来的MAP
     */
    protected Map<GrayWhitePub, List<ClusterNode>> grayWhiteMap = new ConcurrentHashMap<>();

    /**
     * hash因子模式下  。 KEY：hash因子 + “___” +  rpc接口的module name， Value： 对应的目标rpc服务的uip信息
     */
    protected final Map<String, UsingIpPort> hashHandlerMap = new ConcurrentHashMap<>();

    /**
     * 轮训指针
     */
    private static int pointer = 0;

    /**
     * 获取注册到远程服务nacos服务名
     *
     * @return
     */
    abstract String getBaseServerName();

    @Override
    public ClusterNode getOneLivingNode() {
        int count = allNodes.size();
        if (count == 0) {
            log.warn("@@@ {}: 没有活着的节点了", getBaseServerName());
            return null;
        }
        ClusterNode clusterNode = allNodes.get(pointer % count);
        next();
        return clusterNode;
    }


    @Override
    public ClusterNode getOneLivingNode(GrayWhitePub pub) {

        int count = allNodes.size();
        if (count == 0) {
            log.warn("@@@ {}: 没有活着的节点了. pubValue:{}.", getBaseServerName(), pub.value);
            return null;
        }

        List<ClusterNode> nodes = grayWhiteMap.get(pub);
        if (CollectionUtils.isEmpty(nodes)) {
            nodes = new ArrayList<>();
            //从allNodes中挑选出符合 GrayWhitePub 条件的节点来
            for (ClusterNode node : allNodes) {
                if (node.getPubValue() == pub.value) {
                    nodes.add(node);
                }
            }
            grayWhiteMap.put(pub, nodes);
        }

        int size = nodes.size();
        if (size == 0) {
            log.warn("没有活着的[" + pub + "]模式的节点了：{}", this.getBaseServerName());
            return null;
        }

        ClusterNode clusterNode = nodes.get(pointer % size);
        next();
        return clusterNode;
    }

    @Override
    public List<ClusterNode> getAllLivingNode() {
        return allNodes;
    }

    @Override
    public List<ClusterNode> getAllLivingNode(GrayWhitePub pub) {
        if (Objects.isNull(pub)) {
            return allNodes;
        }
        List<ClusterNode> nodes = grayWhiteMap.get(pub);
        if (CollectionUtils.isEmpty(nodes)) {
            nodes = new ArrayList<>();
        }
        return nodes;
    }

    @Override
    public int countNodes() {
        return allNodes.size();
    }

    @Override
    public int countNodes(GrayWhitePub pub) {
        List<ClusterNode> nodes = grayWhiteMap.get(pub);
        if (CollectionUtils.isNotEmpty(nodes)) {
            return nodes.size();
        }
        return 0;
    }

    @Override
    public void addNodeActivityListener(NodeActivityListener listener) {
        observers.add(listener);
    }

    @Override
    public void updateIpPorts(List<ClusterNode> nodes) {
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("@@@ updateIpPorts, nodes.size = {}", nodes.size());
        }
        allNodes.clear();
        grayWhiteMap.clear();

        allNodes = nodes.stream().filter(ClusterNode::getAlive).peek(node -> {
            if (!BaseStringConstants.DEFAULT_HASH_FACTOR.equals(node.getHashFactor())) {
                //更新hash路由
                String key = genTmpKey(node.getHashFactor(), node.getNameEn());
                hashHandlerMap.put(key, node.getUip());
            }
        }).collect(Collectors.toList());

        log.info("hashHandlerMap = {}", JsonUtil.toJson(hashHandlerMap));
        //刷新下灰白分组MAP
        List<ClusterNode> grayNodes = new ArrayList<>();
        List<ClusterNode> whiteNodes = new ArrayList<>();
        for (ClusterNode node : allNodes) {
            if (node.getPubValue() == GrayWhitePub.GRAY.value) {
                grayNodes.add(node);
            } else if (node.getPubValue() == GrayWhitePub.WHITE.value) {
                whiteNodes.add(node);
            }
        }
        grayWhiteMap.put(GrayWhitePub.GRAY, grayNodes);
        grayWhiteMap.put(GrayWhitePub.WHITE, whiteNodes);

    }

    @Override
    public UsingIpPort pickupHashFactor(String value, String hashFactor) {
        if (hashHandlerMap.isEmpty()) {
            synchronized (hashHandlerMap) {
                if (hashHandlerMap.isEmpty()) {
                    List<ClusterNode> newNodes;
                    try {
                        String serverName = getBaseServerName();
                        NamingService namingService = NamingServiceClient.getNamingService();
                        List<Instance> instances = namingService.getAllInstances(serverName);
                        if (CollectionUtils.isEmpty(instances)) {
                            throw new IllegalArgumentException("@@@ 远程nacos服务没发现服务名: " + serverName + " 的实例, 请检查配置是否正确");
                        }
                        newNodes = instances.stream().map(ClusterNode::copy).collect(Collectors.toList());
                        updateIpPorts(newNodes);
                    } catch (Exception e) {
                        log.warn("@@@ retryRead nacos instances, failure.", e);
                    }
                }
            }
        } else {
            log.debug("hashHandlerMap not EMPTY: {}", JsonUtil.toJson(hashHandlerMap));
        }
        final String key = genTmpKey(hashFactor, value);
        return hashHandlerMap.get(key);
    }

    @Override
    public void close() {
        try {
            NamingService namingService = NamingServiceClient.getNamingService();
            if (namingService != null) {
                namingService.shutDown();
                NamingServiceClient.close();
            }
        } catch (NacosException e) {
            log.error(e.getErrMsg(), e);
        }
    }

    protected String genTmpKey(String hashFactor, String nameEn) {
        return hashFactor.concat("___").concat(nameEn);
    }


    @Component
    public class ServerListChangeEventListener extends Subscriber<ServerlistChangeEvent> {

        public ServerListChangeEventListener() {
            log.info("@@@ register ServerListChangeEventListener.");
        }

        @PostConstruct
        private void post() {
            NotifyCenter.registerSubscriber(this);
        }

        @Override
        public void onEvent(ServerlistChangeEvent event) {
            log.info("@@@ received ServerlistChangeEvent start, loadServerNode begin. {}", JsonUtil.toJson(event));
            int count = loadServerNode();
            log.info("@@@ loadServerNode end, count :{}", count);
        }

        @Override
        public Class<? extends Event> subscribeType() {
            return ServerlistChangeEvent.class;
        }
    }


    @Component
    public class InstancesChangeEventListener extends Subscriber<InstancesChangeEvent> {

        public InstancesChangeEventListener() {
            log.info("@@@ register InstancesChangeEventListener.");
        }

        @PostConstruct
        private void post() {
            NotifyCenter.registerSubscriber(this);
        }


        @Override
        public void onEvent(InstancesChangeEvent event) {
            log.info("@@@ received InstancesChangeEvent, loadServerNode begin. {}", JsonUtil.toJson(event));
            int count = loadServerNode();
            log.info("@@@ loadServerNode end, count :{}", count);
        }

        @Override
        public Class<? extends Event> subscribeType() {
            return InstancesChangeEvent.class;
        }
    }

    /**
     * 加载当前服务的节点实例信息
     * @return 发挥节点实例个数
     */
    protected int loadServerNode() {
        //获取NamingService
        NamingService namingService = NamingServiceClient.getNamingService();
        List<ClusterNode> newNodes = new ArrayList<>();
        try {
            //服务名交给子类注册的时候赋值
            String serverName = getBaseServerName();
            //根据服务名获取实例列表
            List<Instance> instances = namingService.getAllInstances(serverName);
            //断言
            Assert.notEmpty(instances, "@@@ 远程nacos服务没发现服务名: " + serverName + " 的实例, 请检查配置是否正确");
            newNodes = instances.stream().map(ClusterNode::copy).collect(Collectors.toList());
            //更新节点信息
            updateIpPorts(newNodes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (observers.size() > 0) {
                List<ClusterNode> uipListGray = this.grayWhiteMap.get(GrayWhitePub.GRAY);
                List<ClusterNode> uipListWhite = this.grayWhiteMap.get(GrayWhitePub.WHITE);
                observers.forEach(e -> e.onAction(uipListGray, uipListWhite));
            }
        }

        return newNodes.size();
    }


    /**
     * 轮训指针后移
     */
    private void next() {
        pointer++;
        if (pointer == 9999) {
            pointer = 0;
        }
    }




}
