package com.hqy.rpc.regist;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.fundation.common.base.lang.BaseStringConstants;
import com.hqy.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 定制化服务节点信息
 * @author qy
 * @date 2021-08-13 10:21
 */
@Data
public class ClusterNode extends Node {

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


    public ClusterNode() {
        setCreated(new Date());
        setPubValue(GrayWhitePub.GRAY.value);
    }

    public ClusterNode(Instance instance) {
        if (Objects.isNull(instance)) {
            throw new IllegalArgumentException("nacos instance is null.");
        }
        String ip = instance.getIp();
        String nameEn = instance.getServiceName();
        String nodeId = instance.getInstanceId();
        //原数据
        Map<String, String> metadata = instance.getMetadata();
        if (metadata == null || metadata.isEmpty()) {
            throw new IllegalArgumentException("registry nacos instance has error, metadata is null -> ip = " + ip + "nameEn = " + nameEn);
        }
        String uipString = metadata.get("uip");
        UsingIpPort usingIpPort = JsonUtil.toBean(uipString, UsingIpPort.class);

    }


    public static ClusterNode convert2Node(Instance instance) {


        String ip = instance.getIp();
        String nameEn = instance.getServiceName();
        String nodeId = instance.getInstanceId();
        int port = instance.getPort();

        //原数据
        Map<String, String> metadata = instance.getMetadata();

        int usingPort = 0;
        if (StringUtils.isNotBlank(metadata.get("usingPort"))) {
            usingPort = Integer.parseInt(metadata.get("usingPort"));
        }

        String name = metadata.get("name");
        Date created = new Date(Long.parseLong(metadata.get("created")));

        int port2 = 0;
        if (StringUtils.isNotBlank(metadata.get("port2"))) {
            port2 = Integer.parseInt(metadata.get("port2"));
        }

        int hash = 0;
        if (StringUtils.isNotBlank(metadata.get("hash"))) {
            hash = Integer.parseInt(metadata.get("hash"));
        }

        int pubValue = 0;
        if (StringUtils.isNotBlank(metadata.get("pubValue"))) {
            pubValue = Integer.parseInt(metadata.get("pubValue"));
        }

        boolean alive = instance.isHealthy() && instance.isEnabled();

        int index = 0;
        if (StringUtils.isNotBlank(metadata.get("index"))) {
            index = Integer.parseInt(metadata.get("index"));
        }

        return ClusterNode.builder().uip(new UsingIpPort(ip, usingPort, index))
                                    .nameEn(nameEn).name(name).nodeId(nodeId)
                                    .env(env).created(created).port(port).port2(port2)
                                    .hash(hash).pubValue(pubValue).alive(alive).build();
    }
}
