package com.hqy.cloud.registry.common.deploy;

import com.hqy.cloud.registry.common.model.DeployModel;

/**
 * ModelDeployerListener.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 13:34
 */
public interface ModelDeployerListener<T extends DeployModel> {

    /**
     * on destroy do something.
     * @param deployModel deploy model
     */
    void onDestroy(T deployModel);

}
