package com.hqy.cloud.rpc.dubbo.deploy;

import com.hqy.cloud.common.base.lang.DeployComponent;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.common.model.DeployModel;

/**
 * dubbo客户端model类
 * @author qiyuan.hong
 * @date 2024/7/9
 * @version 1.0
 */
public class DubboConsumerDeployModel extends DeployModel {

    public DubboConsumerDeployModel(ApplicationModel model) {
        super(model);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public String getModelName() {
        return DeployComponent.DUBBO_CONSUMER_COMPONENT.name;
    }
}
