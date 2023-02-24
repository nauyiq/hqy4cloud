package com.hqy.rpc.registry.api;

import com.hqy.rpc.api.service.RPCModelService;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.registry.RegistryService;
import com.hqy.util.AssertUtil;

import static com.hqy.rpc.common.CommonConstants.DEFAULT_DELAY_NOTIFICATION_TIME;
import static com.hqy.rpc.common.CommonConstants.REGISTRY_DELAY_NOTIFICATION_KEY;

/**
 * 注册中心
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 16:24
 */
public interface Registry extends RegistryService, RPCModelService {

    /**
     * 返回当前节点注册到注册中心的名称
     * @return application name.
     */
    String getServiceNameEn();

    /**
     * get registry address.
     * @return registry address.
     */
    default String getRegistryAddress() {
        RPCModel rpcModel = getModel();
        AssertUtil.notNull(rpcModel, "Registry rpcContext is null.");
        return rpcModel.getRegistryAddress();
    }


    /**
     * 获取延迟通知时间
     * @return 延迟通知时间
     */
    default int getNotifyDelay() {
        return getModel().getParameter(REGISTRY_DELAY_NOTIFICATION_KEY, DEFAULT_DELAY_NOTIFICATION_TIME);
    }

    /**
     * not retry register when register throw exception.
     * @param rpcModel register information
     */
    default void reExportRegister(RPCModel rpcModel) {
        register(rpcModel);
    }

    /**
     * not retry unregister when unregister throw exception.
     * @param rpcModel unregister information
     */
    default void reExportUnregister(RPCModel rpcModel) {
        unregister(rpcModel);
    }


}
