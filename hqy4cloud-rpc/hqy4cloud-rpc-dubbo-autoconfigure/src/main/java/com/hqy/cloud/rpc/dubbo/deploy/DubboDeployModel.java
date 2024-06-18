package com.hqy.cloud.rpc.dubbo.deploy;

import com.hqy.cloud.common.base.lang.DeployComponent;
import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import com.hqy.cloud.registry.common.model.DeployModel;

/**
 * dubbo服务提供者
 * @author qiyuan.hong
 * @date 2024/7/10
 * @version 1.0
 */
public class DubboDeployModel extends DeployModel {

    public DubboDeployModel(ProjectInfoModel model) {
        super(model);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public String getModelName() {
        return DeployComponent.DUBBO_COMPONENT.name;
    }
}
