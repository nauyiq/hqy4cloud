package com.hqy.fundation.common.route;

import com.hqy.rpc.thrift.ex.ThriftRpcHelper;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 某个socket业务节点是否启用节点模式: 如果是, 有几个节点? 环境信息?
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/24 11:07
 */
public class SocketClusterStatus implements Serializable {

    private static final long serialVersionUID = -6308403629096760325L;

    /**
     * 哪个项目
     */
    private String module;

    /**
     * 环境
     */
    private String env;

    /**
     * 开启了几台负载
     */
    private int countMultiNodes = 1;

    /**
     * 是否启用多节点
     */
    private boolean enableMultiWsNode = false;


    private String contextPath = ThriftRpcHelper.DEFAULT_HASH_FACTOR;

    public SocketClusterStatus() {
    }

    public SocketClusterStatus(String module, String env) {
        this.module = module;
        this.env = env;
    }

    public SocketClusterStatus(String module, String env, int countMultiNodes, boolean enableMultiWsNode) {
        this.module = module;
        this.env = env;
        this.countMultiNodes = countMultiNodes;
        this.enableMultiWsNode = enableMultiWsNode;
    }

    public SocketClusterStatus(String module, String env, int countMultiNodes, boolean enableMultiWsNode, String contextPath) {
        this.module = module;
        this.env = env;
        this.countMultiNodes = countMultiNodes;
        this.enableMultiWsNode = enableMultiWsNode;
        this.contextPath = contextPath;
    }

    /**
     * 根据bizId获取socket io的hash值
     * @param bizId 业务id
     * @return hash值
     */
    public int getSocketIoPathHashMod(String bizId) {
        if (!enableMultiWsNode) {
            throw new IllegalStateException("@@@ SocketHashContext.enableMultiWsNode is false.");
        }
        if (StringUtils.isBlank(bizId)) {
            throw new IllegalStateException("@@@ bizId can not empty.");
        }
        int hashcode = Math.abs(bizId.hashCode());
        return hashcode % countMultiNodes;
    }

    public int getCountMultiNodes() {
        return countMultiNodes;
    }

    public void setCountMultiNodes(int countMultiNodes) {
        this.countMultiNodes = countMultiNodes;
    }

    public boolean isEnableMultiWsNode() {
        return enableMultiWsNode;
    }

    public void setEnableMultiWsNode(boolean enableMultiWsNode) {
        this.enableMultiWsNode = enableMultiWsNode;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
