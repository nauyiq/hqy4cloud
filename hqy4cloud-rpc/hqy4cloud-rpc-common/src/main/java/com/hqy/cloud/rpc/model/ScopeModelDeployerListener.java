package com.hqy.cloud.rpc.model;

/**
 * ScopeModelDeployerListener.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/1 9:59
 */
public interface ScopeModelDeployerListener<T extends ScopeModel> {

    /**
     * on destroy do something.
     * @param scopeModel scope model.
     */
    void onDestroy(T scopeModel);

}
