package com.hqy.rpc.registry.client;

import com.hqy.base.common.base.lang.Prioritized;
import com.hqy.base.common.base.project.UsingIpPort;
import com.hqy.rpc.registry.api.NodeActivityObserver;
import com.hqy.rpc.common.Node;
import com.hqy.rpc.registry.node.GrayWhitePub;

import java.util.List;

/**
 * 注册中心 服务调用者视角
 * @author qiyuan.hong
 * @date 2021-09-17 18:30
 */
public interface ServerDiscovery extends RegistryService, Prioritized {

    /**
     * 轮训获取一个或者的节点， 负载均衡（不区分灰白度）
     * @return 实例节点对象
     */
    Node getOneLivingNode();

    /**
     * 轮训获取一个或者的节点， 负载均衡
     * @param pub 灰度 or 白度?
     * @return    实例节点对象
     */
    Node getOneLivingNode(GrayWhitePub pub);


    /**
     * 轮询获取所有活着的节点，负载均衡（不区分灰度 白度）
     * @return 实例节点对象列表
     */
    List<Node> getAllLivingNode();

    /**
     * 轮询获取所有活着的节点，负载均衡（区分灰度 白度）
     * @param pub 灰度 or 白度?
     * @return    实例节点对象列表
     */
    List<Node> getAllLivingNode(GrayWhitePub pub);


    /**
     * 获取节点个数
     * @return 节点个数统计（不区分灰度白度）
     */
    int countNodes() ;


    /**
     * 节点个数统计
     * @param pub 灰度 or 白度?
     * @return    节点个数
     */
    int countNodes(GrayWhitePub pub) ;


    /**
     * 刷新节点信息
     * @param nodes 待更新的节点列表
     */
    void updateIpPorts(List<Node> nodes);

    /**
     * hash 路由，获取hash节点
     * @param nameEn     模块名称
     * @param hashFactor hash因子
     * @return           节点ip、端口信息
     */
    UsingIpPort pickupHashFactor(String nameEn, String hashFactor);


    /**
     * 添加nacos节点变化观察者.
     * @param observer
     */
    void addNodeActivityObserver(NodeActivityObserver observer);



    /**
     * 客户端是否关闭
     * @return 是否关闭
     */
    boolean isClose();

    /**
     * 关闭客户端，释放与远程注册中心的长连接
     * @throws Exception
     */
    void close() throws Exception;


}
