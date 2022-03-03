package com.hqy.rpc.regist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hqy.fundation.common.base.lang.ActuatorNodeEnum;
import com.hqy.rpc.thrift.ex.ThriftRpcHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * 定制化服务节点信息
 * 即一个服务对应一个节点信息 可以是网关服务、用户服务、日志服务...
 * @author qy
 * @date 2021-08-13 10:21
 */
@Data
@EqualsAndHashCode(callSuper = false)
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
    private String hashFactor = ThriftRpcHelper.DEFAULT_HASH_FACTOR;

    /**
     * 是生产者还是消费者
     */
    private ActuatorNodeEnum actuatorNode;


    /**
     * 节点创建时间
     */
    private Date created;

    public ClusterNode() {
        setCreated(new Date());
        setPubValue(GrayWhitePub.GRAY.value);
        this.nodeId = UUID.randomUUID().toString().replace("-", "");
    }



}
