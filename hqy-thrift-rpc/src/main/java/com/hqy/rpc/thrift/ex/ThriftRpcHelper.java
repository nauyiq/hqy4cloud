package com.hqy.rpc.thrift.ex;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.fundation.common.base.lang.BaseStringConstants;
import com.hqy.fundation.common.base.project.UsingIpPort;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/2/24 14:45
 */
@Slf4j
public class ThriftRpcHelper {


    public static List<UsingIpPort> convertToUip(List<ClusterNode> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        }
        return nodes.stream().map(ClusterNode::getUip).collect(Collectors.toList());
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
