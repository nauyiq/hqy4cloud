package com.hqy;

import com.hqy.base.common.base.lang.ActuatorNodeEnum;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.rpc.nacos.AbstractNacosClientWrapper;
import com.hqy.rpc.regist.ClusterNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 13:46
 */
@Slf4j
@Component
public class WalletRegistryClient extends AbstractNacosClientWrapper {

    @Override
    public ClusterNode registryProjectClusterNode() {
        //定制化节点信息
        ClusterNode node = new ClusterNode();
        node.setNameEn(MicroServiceConstants.DEMO_WALLET_SERVICE);
        node.setName("分布式事务demo-钱包服务");
        node.setActuatorNode(ActuatorNodeEnum.PROVIDER);
        return node;
    }
}
