package com.hqy.cloud.rpc.dubbo.deploy;

import com.hqy.cloud.common.base.lang.DeployComponent;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.common.model.DeployModel;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;

/**
 * dubbo服务提供者
 * @author qiyuan.hong
 * @date 2024/7/10
 * @version 1.0
 */
@EnableDubbo
public class DubboProviderDeployModel extends DeployModel {

    public DubboProviderDeployModel(ApplicationModel model) {
        super(model);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public String getModelName() {
        return DeployComponent.DUBBO_PROVIDER_COMPONENT.name;
    }
}
