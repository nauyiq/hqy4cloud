package com.hqy.rpc.regist;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hqy.fundation.common.base.lang.BaseStringConstants;
import com.hqy.util.JsonUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 定制化服务节点信息
 * @author qy
 * @date 2021-08-13 10:21
 */
@Data
public class ClusterNode extends Node implements Serializable {

    @JsonIgnore
    private static final long serialVersionUID = -4488621302508088453L;

    /**
     * 节点id - uuid
     */
    private String nodeId;

    /**
     * 当前节点的hash值
     */
    private Integer hash;

    /**
     * 哈希因子，区分集群中的某个节点时使用
     */
    private String hashFactor = BaseStringConstants.DEFAULT_HASH_FACTOR;


    /**
     * 节点创建时间
     */
    private Date created;

    public ClusterNode() {
        setCreated(new Date());
        setPubValue(GrayWhitePub.GRAY.value);
        this.nodeId = UUID.randomUUID().toString().replace("-", "");
    }

    public static ClusterNode copy(Instance instance) {
        if (Objects.isNull(instance)) {
            throw new IllegalArgumentException("nacos instance is null.");
        }
        ClusterNode clusterNode;
        String ip = instance.getIp();
        try {
            //原数据
            Map<String, String> metadata = instance.getMetadata();
            String nodeInfo = metadata.get(BaseStringConstants.NODE_INFO);
            clusterNode = JsonUtil.toBean(nodeInfo, ClusterNode.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("registry nacos instance has error, metadata is null -> ip = " + ip + "nameEn = " + instance.getServiceName());
        }
        return clusterNode;
    }

}
