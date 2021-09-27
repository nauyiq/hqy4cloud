package com.hqy.rpc.nacos;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.rpc.regist.Node;
import com.hqy.rpc.regist.UsingIpPort;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 定制化服务节点信息
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-13 10:21
 */
@Data
@AllArgsConstructor
public class NacosNode extends Node {

    /**
     * 节点id - uuid
     */
    private String nodeId;

    /**
     * 服务的第二个端口， 通道是websocket服务暴露的端口
     */
    private Integer port2;

    /**
     * 当前节点的hash值
     */
    private Integer hash;

    /**
     * 节点创建时间
     */
    private Date created;

    /**
     * 灰白度 默认灰度发布
     */
    private int pubValue;

    private Map<String, String> metaData = new HashMap<>();


    public NacosNode() {
        this.created = new Date();
        pubValue = GrayWhitePub.GRAY.value;
    }

    public static NacosNode convert2Node(Instance instance) {
        NacosNode node = new NacosNode();
        Map<String, String> metadata = instance.getMetadata();

        node.setEnv(EnvironmentConfig.getInstance().getEnvironment());
        node.setName(metadata.get("name"));
        node.setNameEn(instance.getServiceName());
        node.setNodeId(instance.getInstanceId());

        String created = metadata.get("created");
        if (StringUtils.isNotBlank(created)) {
            node.setCreated(new Date(Long.parseLong(created)));
        }

        String port2 = metadata.get("port2");
        if (StringUtils.isNotBlank(port2)) {
            node.setPort2(Integer.valueOf(port2));
        }

        String hash = metadata.get("hash");
        if (StringUtils.isNotBlank(hash)) {
            node.setHash(Integer.valueOf(hash));
        }

        String pubValue = metadata.get("pubValue");
        if (StringUtils.isNotBlank(pubValue)) {
            node.setPubValue(Integer.parseInt(pubValue));
        }

        node.setAlive(instance.isHealthy());

        String ip = instance.getIp();
        int port = instance.getPort();
        int index = 0;
        if (StringUtils.isNotBlank(metadata.get("index"))) {
            index = Integer.parseInt(metadata.get("index"));
        }

        UsingIpPort usingIpPort = new UsingIpPort(ip, port, index);

        node.setUip(usingIpPort);

        return node;
    }
}
