package com.hqy;

import com.facebook.swift.service.ThriftServer;
import com.hqy.base.common.base.lang.ActuatorNodeEnum;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.base.common.base.project.UsingIpPort;
import com.hqy.rpc.nacos.AbstractNacosClientWrapper;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.util.AssertUtil;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 14:19
 */
@Slf4j
@Component
public class StorageRegistryClient extends AbstractNacosClientWrapper {

    @Override
    public ClusterNode registryProjectClusterNode() {
        //判断RPC服务是否启动
        ThriftServer tServer = SpringContextHolder.getBean(ThriftServer.class);
        boolean running = tServer.isRunning();
        if (!running) {
            //如果没有扫描到server 手动启动一下
            tServer.start();
        }
        log.info("@@@ Get ThriftServer success, running:{}", running);

        StorageThriftServer thriftServer = SpringContextHolder.getBean(StorageThriftServer.class);
        UsingIpPort usingIpPort = thriftServer.getUsingIpPort();

        AssertUtil.notNull(usingIpPort, "System error, Bind rpc port fail. please check thrift service");

        //定制化节点信息
        ClusterNode node = new ClusterNode();
        node.setNameEn(MicroServiceConstants.DEMO_STORAGE_SERVICE);
        node.setName("分布式事务demo-库存服务");
        node.setUip(usingIpPort);
        node.setActuatorNode(ActuatorNodeEnum.PROVIDER);
        return node;
    }
}
