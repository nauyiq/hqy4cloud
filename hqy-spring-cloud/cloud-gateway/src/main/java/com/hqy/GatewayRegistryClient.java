package com.hqy;

import com.hqy.base.common.base.lang.ActuatorNodeEnum;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.rpc.nacos.AbstractNacosClientWrapper;
import com.hqy.rpc.regist.ClusterNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 所有需要暴露出RPC的服务 必须继承AbstractNacosClientWrapper类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 10:22
 */
@Slf4j
@Component
public class GatewayRegistryClient extends AbstractNacosClientWrapper  {

    @Override
    public ClusterNode registryProjectClusterNode() {
        //定制化节点信息
        ClusterNode node = new ClusterNode();
        node.setNameEn(MicroServiceConstants.GATEWAY);
        node.setName("全局网关服务");
        node.setActuatorNode(ActuatorNodeEnum.CONSUMER);
        return node;
    }

}
