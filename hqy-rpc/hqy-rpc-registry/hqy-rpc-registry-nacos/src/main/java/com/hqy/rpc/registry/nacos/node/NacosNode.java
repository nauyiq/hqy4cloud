package com.hqy.rpc.registry.nacos.node;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.base.Objects;
import com.hqy.base.common.base.lang.ActuatorNodeEnum;
import com.hqy.base.common.base.project.UsingIpPort;
import com.hqy.rpc.common.Node;
import com.hqy.util.AssertUtil;
import com.hqy.util.CommonDateUtil;
import com.hqy.util.JsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.text.ParseException;
import java.util.Map;

import static com.hqy.rpc.registry.nacos.MetadataContext.MetadataKey.*;

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
     * 组名
      */
    private String group;

    /**
     * 是否是临时实例
     */
    private boolean ephemeral;

    public NacosNode() {
        super();
        this.instanceId = UUID.randomUUID().toString();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("instanceId", instanceId)
                .append("group", group)
                .append("ephemeral", ephemeral)
                .append("node", super.toString())
                .toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NacosNode nacosNode = (NacosNode) o;
        return ephemeral == nacosNode.ephemeral && Objects.equal(instanceId, nacosNode.instanceId) && Objects.equal(group, nacosNode.group);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), instanceId, group, ephemeral);
    }


    public static String getGroup(Node node) {
        return getGroup(node, Constants.DEFAULT_GROUP);
    }

    public static String getGroup(Node node, String defaultGroup) {
        if (node instanceof NacosNode) {
            NacosNode nacosNode = (NacosNode) node;
            return StringUtils.isBlank(nacosNode.group) ? defaultGroup : nacosNode.getGroup();
        }
        return defaultGroup;
    }



}
