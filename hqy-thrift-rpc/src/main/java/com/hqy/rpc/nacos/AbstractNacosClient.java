package com.hqy.rpc.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.listener.Event;
import com.hqy.fundation.common.base.lang.BaseIntegerConstants;
import com.hqy.fundation.common.base.project.MicroServiceHelper;
import com.hqy.fundation.common.base.project.UsingIpPort;
import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.rpc.thrift.ex.ThriftRpcHelper;
import com.hqy.util.JsonUtil;
import com.hqy.util.thread.ParentExecutorService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * nacos客户端服务基类 维护了服务节点列表信息
 * 每个服务都会注册一个服务列表到内存里面 用于rpc调度
 * @author qiyuan.hong
 * @date 2021-09-17 17:22
 */
public abstract class AbstractNacosClient implements RegistryClient {

    private static final Logger log = LoggerFactory.getLogger(AbstractNacosClient.class);

    /**
     * 观察者列表
     */
    protected final List<NodeActivityObserver> observers = new ArrayList<>();

    /**
     * 活着的所有节点一览表
     */
    protected static List<ClusterNode> allNodes = new CopyOnWriteArrayList<>();

    /**
     * 活着的节点一览表 ，根据灰白模式 缓存起来的MAP
     */
    protected final Map<GrayWhitePub, List<ClusterNode>> grayWhiteMap = new ConcurrentHashMap<>();

    /**
     * hash因子模式下  。 KEY：hash因子 + “_” +  rpc接口的module name， Value： 对应的目标rpc服务的uip信息
     */
    protected final Map<String, UsingIpPort> hashHandlerMap = new ConcurrentHashMap<>();

    /**
     * 轮训指针
     */
    private static int pointer = 0;

    /**
     * 获取注册到远程服务nacos服务名
     * @return
     */
    public abstract String getServiceNameEn();

    @Override
    public ClusterNode getOneLivingNode() {
        int count = allNodes.size();
        if (count == 0) {
            log.warn("@@@ {}: 没有活着的节点了", getServiceNameEn());
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
            log.warn("@@@ {}: 没有活着的节点了. pubValue:{}.", getServiceNameEn(), pub.value);
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
            log.warn("没有活着的[" + pub + "]模式的节点了：{}", this.getServiceNameEn());
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
    public void addNodeActivityObserver(NodeActivityObserver observer) {
        observers.add(observer);
    }

    @Override
    public void updateIpPorts(List<ClusterNode> nodes) {
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("@@@ updateIpPorts, nodes.size = {}", nodes.size());
        }
        allNodes.clear();
        grayWhiteMap.clear();

        allNodes = nodes.stream().filter(ClusterNode::getAlive).peek(node -> {
            if (!ThriftRpcHelper.DEFAULT_HASH_FACTOR.equals(node.getHashFactor())) {
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
    public UsingIpPort pickupHashFactor(String nameEn, String hashFactor) {
        if (hashHandlerMap.isEmpty()) {
            synchronized (hashHandlerMap) {
                if (hashHandlerMap.isEmpty()) {
                    List<ClusterNode> newNodes = NamingServiceClient.getInstance().loadProjectNodeInfo(getServiceNameEn(),true);
                    if (CollectionUtils.isEmpty(newNodes)) {
                        log.warn("@@@ LoadAllProjectNodeInfo is empty, please check nacos service healthy.");
                        return null;
                    }
                    //重新更新一下节点列表
                    updateIpPorts(newNodes);
                }
            }
        } else {
            log.debug("hashHandlerMap not EMPTY: {}", JsonUtil.toJson(hashHandlerMap));
        }
        final String key = genTmpKey(hashFactor, nameEn);
        return hashHandlerMap.get(key);
    }

    @Override
    public void close() {
        try {
            boolean status = NamingServiceClient.getInstance().status();
            if (!status) {
                NamingServiceClient.getInstance().close();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    protected String genTmpKey(String hashFactor, String nameEn) {
        return hashFactor.concat("_").concat(nameEn);
    }


    protected AbstractNacosClient() {
        synchronized (grayWhiteMap) {
            log.info("@@@ AbstractNacosClient start.");
            boolean result = subscribeNacosEventListener();
            if (result) {
                //为解决第一次异步通知事件延迟的问题，尝试等待1秒;不踩坑不知道。
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                log.error("@@@ subscribeNacosEventListener result is false, check config!");
                throw new RuntimeException("@@@ 订阅nacos事件变化监听器失败...");
            }

        }

    }

    /**
     * 订阅每个服务的InstancesChangeEvent和ServerlistChangeEvent
     * @return 返回结果
     */
    private boolean subscribeNacosEventListener() {
        try {
            // 初始化的时候加载一次节点数据情况；
            int count = loadNodesAndNotifyNodeObserver(null);
            log.info("@@@ 初始化Nacos节点数据情况, 存活节点个数：{}", count);
            Set<String> serviceEnNames = MicroServiceHelper.getServiceEnNames();
            //订阅节点变化监听器
            subscribe(serviceEnNames);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 订阅节点变化监听器
     * @param serviceEnNames
     */
    private void subscribe(Set<String> serviceEnNames) {
        ParentExecutorService.getInstance().execute(() -> {
            for (String serviceEnName : serviceEnNames) {
                try {
                    NamingServiceClient.getInstance().subscribe(serviceEnName, event -> {
                        log.info("@@@ received event, loadNodesAndNotifyNodeObserver begin.");
                        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                            log.info("@@@ event : {}", JsonUtil.toJson(event));
                        }
                        int eventBeforeNodeCount = loadNodesAndNotifyNodeObserver(event);
                        log.info("@@@ loadServerNode end, eventBeforeNodeCount :{}", eventBeforeNodeCount);
                        if (eventBeforeNodeCount == 0) {
                            //当前服务的实例为0....
                            log.warn("Begin to notify ,all server nodes down for {}", serviceEnName);
                        }
                    });
                } catch (NacosException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }


    /**
     * 加载远程服务的所有存活nacos实例到内存中
     * 如果事件不会空 则说明发生了节点变化事件 则通知每个观察者 刷新内存中的服务列表
     * @param event nacos事件
     * @return 所有服务的实例总和
     */
    private int loadNodesAndNotifyNodeObserver(Event event) {
        //加载所有服务在远程服务nacos中的实例
        List<ClusterNode> nodes;
        try {
            nodes = NamingServiceClient.getInstance().loadProjectNodeInfo(getServiceNameEn(),true);
            if (CollectionUtils.isEmpty(nodes)) {
                log.warn("@@@ LoadAllProjectNodeInfo is empty, please check nacos service healthy.");
            }
            //更新节点列表
            this.updateIpPorts(nodes);
        } finally {
            //通知每个观察者 刷新可用节点
            if (Objects.nonNull(event) && CollectionUtils.isNotEmpty(observers)) {
                List<ClusterNode> grayNodes = grayWhiteMap.get(GrayWhitePub.GRAY);
                List<ClusterNode> whiteNodes = grayWhiteMap.get(GrayWhitePub.WHITE);
                for (NodeActivityObserver observer : observers) {
                    observer.onAction(grayNodes, whiteNodes);
                }
            }
        }
        return nodes.size();
    }





    /**
     * 轮训指针后移
     */
    private void next() {
        pointer++;
        if (pointer == BaseIntegerConstants.POINTER) {
            pointer = 0;
        }
    }


}
