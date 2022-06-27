package com.hqy.rpc.registry.node;

import com.google.common.base.Objects;
import com.hqy.base.common.base.lang.ActuatorNodeEnum;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.base.project.UsingIpPort;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 节点实例对象基类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/22 10:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Node implements Serializable {

    private static final long serialVersionUID = -5516600925417294297L;
    /**
     * 节点名称 (中文名)
     */
    private String name;

    /**
     * 节点名称 (英文名)
     */
    private String nameEn;

    /**
     * 当前节点的hash值
     */
    private Integer hash;

    /**
     * 哈希因子，区分集群中的某个节点时使用
     */
    private String hashFactor = StringConstants.DEFAULT;

    /**
     * 是生产者还是消费者
     */
    private ActuatorNodeEnum actuatorNode;

    /**
     * 节点创建时间
     */
    private Date created;

    /**
     * 使用的ip，端口等信息
     */
    private UsingIpPort uip;

    /**
     * 灰白度 默认灰度发布
     */
    private int pubValue;


    /**
     * 当前节点在注册中心是否是脱机状态... true表示存活
     */
    private boolean alive = true;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("nameEn", nameEn)
                .append("hash", hash)
                .append("hashFactor", hashFactor)
                .append("actuatorNode", actuatorNode)
                .append("created", created)
                .append("uip", uip)
                .append("alive", alive)
                .append("pubValue", pubValue)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return pubValue == node.pubValue && alive == node.alive && Objects.equal(name, node.name) && Objects.equal(nameEn, node.nameEn) && Objects.equal(hash, node.hash) && Objects.equal(hashFactor, node.hashFactor) && actuatorNode == node.actuatorNode && Objects.equal(created, node.created) && Objects.equal(uip, node.uip);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, nameEn, hash, hashFactor, actuatorNode, created, uip, pubValue, alive);
    }
}
