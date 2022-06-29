package com.hqy.rpc.registry.client;

import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.base.project.UsingIpPort;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.registry.api.NodeActivityObserver;
import com.hqy.rpc.common.GrayWhitePub;
import com.hqy.rpc.common.Node;
import com.hqy.util.JsonUtil;
import com.hqy.util.thread.ParentExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 抽象的客户端rpc 调用者视角
 * 每个服务会注册一个相应节点实例缓存到内存 并且动态监听刷新内存节点列表 <br/>
 * 只有在RPC调度时会首次初始化对应生产者节点的客户端视角 并且缓存起来.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/22 11:19
 */
@Slf4j
public abstract class AbstractServerDiscovery implements ServerDiscovery {

    protected final String serviceName;
    protected volatile boolean isClose;
    protected Metadata registryMetadata;
    private int pointer;

    /**
     * 观察者列表 - 关心当前服务实例变更情况
     */
    protected final List<NodeActivityObserver> observers = new CopyOnWriteArrayList<>();

    /**
     * 当前服务活着的所有实例节点一览表
     */
    protected static List<Node> allNodes = new CopyOnWriteArrayList<>();

    /**
     * 活着的节点一览表 ，根据灰白模式 缓存起来的MAP
     */
    protected final Map<GrayWhitePub, List<Node>> grayWhiteMap = new ConcurrentHashMap<>();

    /**
     * hash因子模式下  。 KEY：hash因子 + “_” +  rpc接口的module name， Value： 对应的目标rpc服务的uip信息
     */
    protected final Map<String, UsingIpPort> hashNodesMap = new ConcurrentHashMap<>();


    public AbstractServerDiscovery(String serviceName) {
        this.serviceName = serviceName;
        synchronized (grayWhiteMap) {
            log.info("@@@ AbstractRegistryClient start.");
            if (subscribeNodeModifyEventListener()) {
                //为解决第一次异步通知事件延迟的问题，尝试等待1秒;不踩坑不知道。
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                log.error("@@@ subscribeNacosEventListener result is false, check config!");
                throw new RuntimeException("@@@ 注册节点状态变更事件监听器失败...");
            }
        }
    }

    /**
     * 由子类实现 从远程注册中心加载节点信息
     * @param serviceNameEn 服务名
     * @param alive         是否存活 false则返回所有节点信息 包括不健康的节点
     * @return              节点信息列表
     */
    public abstract List<Node> loadProjectNodeInfo(String serviceNameEn, boolean alive);

    /**
     * 由子类实现 订阅远程服务中的节点变更后重新加载节点信息并通知观察者刷新节点
     * @param serviceName 服务名
     */
    public abstract void subscribe(String serviceName);


    /**
     * 订阅节点状态变更事件并注册监听器 由子类实现
     * @return 是否成功
     */
    private boolean subscribeNodeModifyEventListener() {
        try {
            //加载远程节点并且通知观察者
            int count = loadNodesAndNotifyNodeObserver(false);
            log.info("@@@ 初始化Nacos节点数据情况, 存活节点个数：{}", count);
            subscribe(serviceName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }


    /**
     * 订阅节点变化监听器
     * @param serviceNameEn 服务名
     */
    private void subscribe(String... serviceNameEn) {
        //异步订阅节点变化监听器
        ParentExecutorService.getInstance().execute(() -> {
            for (String serviceName : serviceNameEn) {
                try {
                    subscribe(serviceName);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }

    private int loadNodesAndNotifyNodeObserver(boolean notify) {
        List<Node> nodes = new ArrayList<>();
        try {
            nodes = loadProjectNodeInfo(serviceName, true);
            if (CollectionUtils.isEmpty(nodes)) {
                log.warn("@@@ LoadAllProjectNodeInfo is empty, please check nacos service healthy.");
            }
            //更新节点列表
            this.updateIpPorts(nodes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (CollectionUtils.isNotEmpty(observers) && notify) {
                final List<Node> grayNods = grayWhiteMap.get(GrayWhitePub.GRAY);
                final List<Node> whiteNodes = grayWhiteMap.get(GrayWhitePub.WHITE);
                observers.forEach(observer -> observer.onAction(grayNods, whiteNodes));
            }
        }
        return nodes.size();
    }


    @Override
    public Node getOneLivingNode() {
        int count = allNodes.size();
        if (count == 0) {
            log.warn("@@@ {}: 没有活着的节点了", serviceName);
            return null;
        }
        Node clusterNode = allNodes.get(pointer % count);
        pointNext();
        return clusterNode;
    }

    @Override
    public Node getOneLivingNode(GrayWhitePub pub) {
        int count = allNodes.size();
        if (count == 0) {
            log.warn("@@@ {}: 没有活着的节点了. pubValue:{}.", serviceName, pub.value);
            return null;
        }
        List<Node> nodes = grayWhiteMap.get(pub);
        if (CollectionUtils.isEmpty(nodes)) {
            //从allNodes中挑选出符合 GrayWhitePub 条件的节点来
            for (Node node : allNodes) {
                if (node.getPubMode() == pub.value) {
                    nodes.add(node);
                }
            }
            grayWhiteMap.put(pub, nodes);
        }
        int size = nodes.size();
        if (size == 0) {
            log.warn("没有活着的[" + pub + "]模式的节点了：{}", serviceName);
            return null;
        }
        Node node = nodes.get(pointer % size);
        pointNext();
        return node;
    }


    @Override
    public List<Node> getAllLivingNode() {
        return allNodes;
    }

    @Override
    public List<Node> getAllLivingNode(GrayWhitePub pub) {
        if (Objects.isNull(pub)) {
            return allNodes;
        }
        List<Node> nodes = grayWhiteMap.get(pub);
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
        List<Node> nodes = grayWhiteMap.get(pub);
        if (CollectionUtils.isNotEmpty(nodes)) {
            return nodes.size();
        }
        return 0;
    }

    @Override
    public void updateIpPorts(List<Node> nodes) {
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("@@@ updateIpPorts, nodes.size = {}", nodes.size());
        }

        allNodes.clear();
        grayWhiteMap.clear();

        if (CollectionUtils.isEmpty(nodes)) {
            log.warn("@@@ All instances of the {} are offline", serviceName);
            hashNodesMap.clear();
            return;
        }

        allNodes = nodes.stream().filter(Node::isAlive).peek(node -> {
            if (!StringConstants.DEFAULT.equals(node.getHashFactor())) {
                //更新hash路由
                String key = gentHashKey(node.getHashFactor(), node.getNameEn());
                hashNodesMap.put(key, node.getUip());
            }
        }).collect(Collectors.toList());

        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("hashHandlerMap = {}", JsonUtil.toJson(hashNodesMap));
        }

        //刷新下灰白分组MAP
        List<Node> grayNodes = new ArrayList<>();
        List<Node> whiteNodes = new ArrayList<>();
        allNodes.forEach(node -> {
            if (node.getPubMode() == GrayWhitePub.GRAY.value) {
                grayNodes.add(node);
            } else if (node.getPubMode() == GrayWhitePub.WHITE.value) {
                whiteNodes.add(node);
            }
        });
        grayWhiteMap.put(GrayWhitePub.GRAY, grayNodes);
        grayWhiteMap.put(GrayWhitePub.WHITE, whiteNodes);
    }




    @Override
    public UsingIpPort pickupHashFactor(String nameEn, String hashFactor) {
        if (hashNodesMap.isEmpty()) {
            synchronized (hashNodesMap) {
                if (hashNodesMap.isEmpty()) {
                    List<Node> newNodes = loadProjectNodeInfo(serviceName,true);
                    if (CollectionUtils.isEmpty(newNodes)) {
                        log.warn("@@@ LoadAllProjectNodeInfo is empty, please check nacos service healthy.");
                        return null;
                    }
                    //重新更新一下节点列表
                    updateIpPorts(newNodes);
                }
            }
        } else {
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.debug("hashHandlerMap not EMPTY: {}", JsonUtil.toJson(hashNodesMap));
            }
        }
        final String key = gentHashKey(hashFactor, nameEn);
        return hashNodesMap.get(key);
    }

    @Override
    public void addNodeActivityObserver(NodeActivityObserver observer) {
        observers.add(observer);
    }


    protected void pointNext() {
        try {
            pointer++;
        } catch (Exception e) {
            pointer = 0;
        }
    }


    protected String gentHashKey(String hashFactor, String nameEn) {
        return hashFactor.concat("_").concat(nameEn);
    }




}
