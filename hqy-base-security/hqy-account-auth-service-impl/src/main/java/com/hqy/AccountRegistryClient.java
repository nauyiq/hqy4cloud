package com.hqy;

import com.facebook.swift.service.ThriftServer;
import com.hqy.fundation.common.base.lang.ActuatorNodeEnum;
import com.hqy.fundation.common.base.project.MicroServiceConstants;
import com.hqy.fundation.common.base.project.UsingIpPort;
import com.hqy.rpc.nacos.AbstractNacosClientWrapper;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.util.AssertUtil;
import com.hqy.util.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * rpc生产者客户端 将rpc、节点数据注册到nacos中
 * @author qiyuan.hong
 * @date 2022-03-10 21:52
 */
@Component
public class AccountRegistryClient extends AbstractNacosClientWrapper {

    private static final Logger log = LoggerFactory.getLogger(AccountRegistryClient.class);

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

        AccountThriftServer thriftServer = SpringContextHolder.getBean(AccountThriftServer.class);
        UsingIpPort usingIpPort = thriftServer.getUsingIpPort();
        AssertUtil.notNull(usingIpPort, "System error, Bind rpc port fail. please check thrift service");

        //定制化节点信息
        ClusterNode node = new ClusterNode();
        node.setNameEn(MicroServiceConstants.ACCOUNT_SERVICE);
        node.setName("账号服务");
        node.setUip(usingIpPort);
        node.setActuatorNode(ActuatorNodeEnum.PROVIDER);
        return node;
    }
}
