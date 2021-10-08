package com.hqy.rpc.nacos;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.rpc.regist.UsingIpPort;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@AllArgsConstructor
public class NacosNode {


    /**
     * 节点id - uuid
     */
    private String nodeId;

    /**
     * 使用的ip，端口等信息（这里注册rpc服务的端口和ip）
     */
    private UsingIpPort uip;

    /**
     * http服务端口
     */
    private Integer port;

    /**
     * 服务的第二个端口， 通道是websocket服务暴露的端口
     * -1 表示当前服务没有暴露端口2
     */
    private Integer port2 = -1;

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

    /**
     * 默认的hash因子
     */
    public static final String DEFAULT_HASH_FACTOR = "default";

    /**
     * 当前节点在注册中心是否是脱机状态... true表示存活
     */
    private Boolean alive = true;

    /**
     * 当前服务所属环境
     */
    private String env;

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
    private String hashFactor = DEFAULT_HASH_FACTOR;


    public NacosNode() {
        this.created = new Date();
        pubValue = GrayWhitePub.GRAY.value;
    }

    public static NacosNode convert2Node(Instance instance) {


        String ip = instance.getIp();
        String nameEn = instance.getServiceName();
        String nodeId = instance.getInstanceId();
        String env = EnvironmentConfig.getInstance().getEnvironment();
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

        return NacosNode.builder().uip(new UsingIpPort(ip, usingPort, index))
                                    .nameEn(nameEn).name(name).nodeId(nodeId)
                                    .env(env).created(created).port(port).port2(port2)
                                    .hash(hash).pubValue(pubValue).alive(alive).build();
    }
}
