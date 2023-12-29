package com.hqy.cloud.rpc.registry.api;

import com.hqy.cloud.rpc.model.RegistryInfo;
import com.hqy.cloud.rpc.service.RPCModelService;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.util.AssertUtil;

import static com.hqy.cloud.rpc.CommonConstants.DEFAULT_DELAY_NOTIFICATION_TIME;
import static com.hqy.cloud.rpc.CommonConstants.REGISTRY_DELAY_NOTIFICATION_KEY;

/**
 * 注册中心
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 16:24
 */
public interface RPCRegistry extends RPCRegistryService, RPCModelService {

    /**
     * return registry name.
     * @return registry name.
     */
    String getName();

    /**
     * 返回当前节点注册到注册中心的名称
     * @return application name.
     */
    String getServiceNameEn();

    /**
     * 获取当前注册中心信息
     * @return {@link  RegistryInfo}
     */
    RegistryInfo getRegistryInfo();

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
