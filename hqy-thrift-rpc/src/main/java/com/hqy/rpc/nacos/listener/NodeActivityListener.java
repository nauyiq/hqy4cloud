package com.hqy.rpc.nacos.listener;

import com.hqy.rpc.regist.ClusterNode;

import java.util.List;

/**
 * Nacos 节点事件 变化监听动作
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-09-18 15:03
 */
public interface NodeActivityListener {


    /**
     * 当发生ServerlistChangeEvent, InstancesChangeEvent 通知最新活着的节点
     * 刷新客户端内部对象池.
     * @param grayNodes 最新活着的灰色节点
     * @param whiteNodes 最新活着的白色节点
     */
    void onAction(List<ClusterNode> grayNodes, List<ClusterNode> whiteNodes);

}
