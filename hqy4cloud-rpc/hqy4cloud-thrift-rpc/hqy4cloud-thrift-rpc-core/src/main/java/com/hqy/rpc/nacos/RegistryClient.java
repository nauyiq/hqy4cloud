/*
package com.hqy.rpc.nacos;

import com.hqy.base.common.base.project.UsingIpPort;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.rpc.regist.GrayWhitePub;

import java.util.List;

*/
/**
 * 注册中心 服务调用者视角
 * @author qiyuan.hong
 * @date 2021-09-17 18:30
 *//*

public interface RegistryClient {

    */
/**
     * 轮训获取一个或者的节点， 负载均衡（不区分灰白度）
     * @return
     *//*

    ClusterNode getOneLivingNode();

    */
/**
     * 轮训获取一个或者的节点， 负载均衡
     * @param pub 需要区别是灰度还是白度
     * @return
     *//*

    ClusterNode getOneLivingNode(GrayWhitePub pub);

    */
/**
     * 返回当前节点注册到注册中心的名称
     * @return
     *//*

    String getServiceNameEn();

    */
/**
     * 轮询获取所有活着的节点，负载均衡（不区分灰度 白度）
     * @return
     *//*

    List<ClusterNode> getAllLivingNode();

    */
/**
     * 轮询获取所有活着的节点，负载均衡（区分灰度 白度）
     * @param pub
     * @return
     *//*

    List<ClusterNode> getAllLivingNode(GrayWhitePub pub);


    */
/**
     * 获取节点个数
     * @return 节点个数统计（不区分灰度白度）
     *//*

    int countNodes() ;


    */
/**
     *  节点个数统计
     * @param pub 区分灰度白度
     * @return
     *//*

    int countNodes(GrayWhitePub pub) ;


    */
/**
     * 刷新节点信息（通常是由nacos收到变更事件后）
     * @param nodes
     *//*

    void updateIpPorts(List<ClusterNode> nodes);

    */
/**
     * hash 路由，获取hash节点
     * @param nameEn 模块名称
     * @param hashFactor hash因子
     * @return
     *//*

    UsingIpPort pickupHashFactor(String nameEn, String hashFactor);


    */
/**
     * 添加nacos节点变化观察者.
     * @param observer
     *//*

    void addNodeActivityObserver(NodeActivityObserver observer);


    */
/**
     * 关闭nacos客户端，释放长连接
     *//*

    void close();


}
*/
