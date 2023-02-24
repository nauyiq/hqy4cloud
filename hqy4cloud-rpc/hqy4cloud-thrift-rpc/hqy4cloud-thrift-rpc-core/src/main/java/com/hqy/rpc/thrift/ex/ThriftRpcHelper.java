/*
package com.hqy.rpc.thrift.ex;

import com.alibaba.cloud.nacos.NacosServiceInstance;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.base.project.UsingIpPort;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.util.JsonUtil;
import com.hqy.util.spring.ProjectContextInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cloud.client.ServiceInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

*/
/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/2/24 14:45
 *//*

@Slf4j
public class ThriftRpcHelper {

    */
/**
     * 默认的hash因子
     *//*

    public static final String DEFAULT_HASH_FACTOR = "default";


    */
/**
     * hashFactor  convert UsingIpPort
     * @param hashFactor 哈希因子 default or ip:rpcPort
     * @return 节点ip
     *//*

    public static UsingIpPort convertHash(String hashFactor) {
        try {
            String[] split = hashFactor.split(StringConstants.Symbol.COLON);
            int port = Integer.parseInt(split[1]);
            return new UsingIpPort(split[0], 0, port, 0);
        } catch (Exception e) {
            log.warn("@@@ [{}] 不是ip:rpcPort的格式, 格式化失败", hashFactor);
            return null;
        }
    }

    public static String genHashFactor(String ip, String port) {
        return ip.concat(StringConstants.Symbol.COLON).concat(port);
    }

    */
/**
     * ClusterNode -> UsingIpPort
     * @param nodes ClusterNodeList
     * @return UsingIpPortList
     *//*

    public static List<UsingIpPort> convertToUip(List<ClusterNode> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return new ArrayList<>();
        }
        return nodes.stream().map(ClusterNode::getUip).collect(Collectors.toList());
    }


    */
/**
     * instance -> ClusterNode
     * @param instance nacos节点实例
     * @return ClusterNode
     *//*

    public static ClusterNode copy(Instance instance) {
        if (Objects.isNull(instance)) {
            throw new IllegalArgumentException("nacos instance is null.");
        }
        ClusterNode clusterNode;
        String ip = instance.getIp();
        try {
            //原数据
            Map<String, String> metadata = instance.getMetadata();
            String nodeInfo = metadata.get(ProjectContextInfo.NODE_INFO);
            clusterNode = JsonUtil.toBean(nodeInfo, ClusterNode.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("registry nacos instance has error, metadata is null -> ip = " + ip + "nameEn = " + instance.getServiceName());
        }
        return clusterNode;
    }

    public static NacosServiceInstance copy(int socketPort, ServiceInstance serviceInstance) {
        NacosServiceInstance nacosServiceInstance = new NacosServiceInstance();
        nacosServiceInstance.setHost(serviceInstance.getHost());
        nacosServiceInstance.setServiceId(serviceInstance.getServiceId());
        nacosServiceInstance.setMetadata(serviceInstance.getMetadata());
        nacosServiceInstance.setPort(socketPort);
        nacosServiceInstance.setSecure(serviceInstance.isSecure());
        return nacosServiceInstance;
    }

}
*/
