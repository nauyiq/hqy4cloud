package com.hqy.rpc.common;

import cn.hutool.core.map.MapUtil;
import com.hqy.base.common.base.lang.ActuatorNodeEnum;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.base.project.UsingIpPort;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 节点实例对象基类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/22 10:12
 */
@Data
public abstract class Node implements Serializable {

    private static final transient long serialVersionUID = -5516600925417294297L;

    /**
     * 节点名称 (中文名)
     */
    private String name;

    /**
     * 节点名称 (英文名)
     */
    private String nameEn;

    /**
     * 哈希因子，区分集群中的某个节点时使用
     */
    private String hashFactor;

    /**
     * 是生产者还是消费者
     */
    private ActuatorNodeEnum actuatorNode;


    /**
     * 使用的ip，端口等信息
     */
    private UsingIpPort uip;

    /**
     * 灰白度 默认灰度发布
     */
    private int pubMode;

    /**
     * 服务的权重 用于rpc调用时负载均衡算法
     */
    private int weight;


    /**
     * 当前节点在注册中心是否是脱机状态... true表示存活
     */
    private boolean alive = true;

    /**
     * 节点创建时间
     */
    private Date created;


    public Node() {
        this.created = new Date();
    }

    /**
     * expansion params
     */
    protected Map<String, String> ex = MapUtil.newHashMap(8);

    public Node(String name, String nameEn, ActuatorNodeEnum actuatorNode, UsingIpPort uip, int pubMode) {
        this(name, nameEn, StringConstants.DEFAULT, actuatorNode, uip, pubMode);
    }

    public Node(String name, String nameEn, String hashFactor, ActuatorNodeEnum actuatorNode, UsingIpPort uip, int pubMode) {
        this.name = name;
        this.nameEn = nameEn;
        this.hashFactor = hashFactor;
        this.actuatorNode = actuatorNode;
        this.uip = uip;
        this.pubMode = pubMode;
        this.created = new Date();
    }

    public Node(String name, String nameEn, String hashFactor, ActuatorNodeEnum actuatorNode, UsingIpPort uip, int pubMode, Map<String, String> ex) {
        this.name = name;
        this.nameEn = nameEn;
        this.hashFactor = hashFactor;
        this.actuatorNode = actuatorNode;
        this.uip = uip;
        this.pubMode = pubMode;
        this.created = new Date();
        this.ex = ex;
    }


    public String getParameter(String key, String defaultValue) {
        String value = ex.get(key);
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    public String getHost() {
        return uip == null ? null : uip.getIp();
    }

    public int getPort() {
        return uip == null ? 0 : uip.getRpcPort();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("nameEn", nameEn)
                .append("hashFactor", hashFactor)
                .append("actuatorNode", actuatorNode)
                .append("uip", uip)
                .append("pubValue", pubMode)
                .append("alive", alive)
                .append("created", created)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return pubMode == node.pubMode && alive == node.alive && Objects.equals(name, node.name) && Objects.equals(nameEn, node.nameEn) && Objects.equals(hashFactor, node.hashFactor) && actuatorNode == node.actuatorNode && Objects.equals(uip, node.uip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nameEn, hashFactor, actuatorNode, uip, pubMode, alive);
    }


}
