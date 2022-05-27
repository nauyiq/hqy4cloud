package com.hqy;

import com.hqy.base.common.base.lang.ActuatorNodeEnum;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.rpc.nacos.AbstractNacosClientWrapper;
import com.hqy.rpc.regist.ClusterNode;
import org.springframework.stereotype.Component;

/**
 * rpc生产者客户端 将rpc、节点数据注册到nacos中
 * @author qiyuan.hong
 * @date 2022-03-10 21:52
 */
@Component
public class AccountRegistryClient extends AbstractNacosClientWrapper {

    @Override
    public ClusterNode registryProjectClusterNode() {
        //定制化节点信息
        ClusterNode node = new ClusterNode();
        node.setNameEn(MicroServiceConstants.ACCOUNT_SERVICE);
        node.setName("账号服务");
        node.setActuatorNode(ActuatorNodeEnum.PROVIDER);
        return node;
    }
}
