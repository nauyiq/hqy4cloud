package com.hqy;

import com.facebook.swift.service.ThriftServer;
import com.hqy.fundation.common.base.lang.ActuatorNodeEnum;
import com.hqy.fundation.common.base.project.MicroServiceConstants;
import com.hqy.fundation.common.base.project.UsingIpPort;
import com.hqy.rpc.nacos.AbstractNacosClientWrapper;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.util.AssertUtil;
import com.hqy.util.spring.SpringContextHolder;
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
    public ClusterNode setProjectClusterNode() {

        GatewayThriftServer gatewayThriftServer = SpringContextHolder.getBean(GatewayThriftServer.class);

        //获取AbstractThriftServer中的节点uip信息
        UsingIpPort usingIpPort = gatewayThriftServer.getUsingIpPort();
        AssertUtil.notNull(usingIpPort, "System error, Bind rpc port fail. please check thrift service");

        //定制化节点信息
        ClusterNode node = new ClusterNode();
        node.setNameEn(MicroServiceConstants.GATEWAY);
        node.setName("全局网关服务");
        node.setUip(usingIpPort);
        node.setActuatorNode(ActuatorNodeEnum.CONSUMER);

        return node;
    }

}
