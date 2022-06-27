package com.hqy.rpc.registry.nacos.node;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.base.Objects;
import com.hqy.rpc.registry.node.Node;
import com.hqy.util.AssertUtil;
import com.hqy.util.JsonUtil;
import com.hqy.util.spring.ProjectContextInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/22 10:42
 */
@Data
@Slf4j
public class NacosNode extends Node {

    private static final long serialVersionUID = -8208556299998584445L;
    /**
     * 节点id
     */
    private String instanceId;

    /**
     * 是否是临时实例
     */
    private boolean ephemeral;


    public static Node instanceConvert(Instance instance) {
        AssertUtil.notNull(instance, "Convert instance to node failure, nacos service instance is null.");
        Map<String, String> metadata = instance.getMetadata();
        String nodeInfo = metadata.get(ProjectContextInfo.NODE_INFO);
        try {
            return JsonUtil.toBean(nodeInfo, NacosNode.class);
        } catch (Exception e) {
            log.error("Convert instance to node error, nodeInfo:{}", nodeInfo);
            throw new IllegalArgumentException("Convert instance to node error.");
        }

    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("instanceId", instanceId)
                .append("ephemeral", ephemeral)
                .append("super", super.toString())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NacosNode nacosNode = (NacosNode) o;
        return ephemeral == nacosNode.ephemeral && Objects.equal(instanceId, nacosNode.instanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), instanceId, ephemeral);
    }
}
