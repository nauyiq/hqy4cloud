package com.hqy.rpc.nacos;

import com.hqy.rpc.nacos.listener.NodeActivityListener;
import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.rpc.regist.Node;
import com.hqy.rpc.regist.UsingIpPort;

import java.util.List;

/**
 * RPC调用者视角 内置当前服务注册NamingService客户端。
 * 请务必 先继承 AbstractNacosClient 不要自己实现。
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-09-17 18:30
 */
public interface NacosClient {

    /**
     * 轮训获取一个或者的节点， 负载均衡（不区分灰白度）
     * @return
     */
    NacosNode getOneLivingNode();

    /**
     * 轮训获取一个或者的节点， 负载均衡
     * @param pub 需要区别是灰度还是白度
     * @return
     */
    NacosNode getOneLivingNode(GrayWhitePub pub);


    /**
     * 轮询获取所有活着的节点，负载均衡（不区分灰度 白度）
     * @return
     */
    List<NacosNode> getAllLivingNode();

    /**
     * 轮询获取所有活着的节点，负载均衡（区分灰度 白度）
     * @param pub
     * @return
     */
    List<NacosNode> getAllLivingNode(GrayWhitePub pub);


    /**
     * @return 节点个数统计（不区分灰度白度）
     */
    int countNodes() ;


    /**
     *  节点个数统计
     * @param pub 区分灰度白度
     * @return
     */
    int countNodes(GrayWhitePub pub) ;


    /**
     * 刷新节点信息（通常是由nacos收到变更事件后）
     * @param nodes
     */
    public void updateIpPorts(List<NacosNode> nodes);

    /**
     * hash 路由，获取hash节点
     * @param value 模块名称
     * @param hashFactor hash因子
     * @return
     */
    public UsingIpPort pickupHashFactor(String value, String hashFactor);


    /**
     * 添加nacos节点变化监听者.
     * @param listener
     */
    void addNodeActivityListener(NodeActivityListener listener);

    /**
     * 关闭nacos客户端，释放长连接
     */
    void close();


}
