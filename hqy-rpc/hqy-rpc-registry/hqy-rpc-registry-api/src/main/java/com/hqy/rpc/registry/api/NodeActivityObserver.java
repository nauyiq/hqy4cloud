package com.hqy.rpc.registry.api;

import com.hqy.rpc.common.Node;

import java.util.List;

/**
 * 节点事件 变化监听动作 观察者接口
 * @author qiyuan.hong
 * @date 2021-09-18 15:03
 */
public interface NodeActivityObserver {


    /**
     * 当节点发生变化时 通知每个活着的节点
     * nacos服务 ServerlistChangeEvent, InstancesChangeEvent 通知最新活着的节点
     * 刷新客户端内部对象池.
     * @param grayNodes 最新活着的灰色节点
     * @param whiteNodes 最新活着的白色节点
     */
    void onAction(List<Node> grayNodes, List<Node> whiteNodes);

}
