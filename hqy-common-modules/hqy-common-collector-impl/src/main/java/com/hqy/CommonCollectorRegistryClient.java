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
 * @date 2022/3/1 17:28
 */
@Slf4j
@Component
public class CommonCollectorRegistryClient extends AbstractNacosClientWrapper {

    @Override
    public ClusterNode setProjectClusterNode() {
        //判断RPC服务是否启动
        ThriftServer tServer = SpringContextHolder.getBean(ThriftServer.class);
        boolean running = tServer.isRunning();
        if (!running) {
            //如果没有扫描到server 手动启动一下
            tServer.start();
        }

        log.info("@@@ Get ThriftServer success, running:{}", running);
        CommonCollectorThriftServer gatewayThriftServer = SpringContextHolder.getBean(CommonCollectorThriftServer.class);

        //获取AbstractThriftServer中的节点uip信息
        UsingIpPort usingIpPort = gatewayThriftServer.getUsingIpPort();
        AssertUtil.notNull(usingIpPort, "System error, Bind rpc port fail. please check thrift service");

        //定制化节点信息
        ClusterNode node = new ClusterNode();
        node.setNameEn(MicroServiceConstants.COMMON_COLLECTOR);
        node.setName("通用采集服务");
        node.setUip(usingIpPort);
        node.setActuatorNode(ActuatorNodeEnum.PROVIDER);

        return node;
    }
}
