package com.hqy.rpc.registry.api;

import com.hqy.rpc.common.MetaDataService;
import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.registry.client.RegistryService;

import static com.hqy.rpc.common.CommonConstants.*;

/**
 * 注册中心
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 16:24
 */
public interface Registry extends RegistryService, MetaDataService {

    /**
     * 获取延迟通知时间
     * @return 延迟通知时间
     */
    default int getNotifyDelay() {
        return getMetadata().getParameter(REGISTRY_DELAY_NOTIFICATION_KEY, DEFAULT_DELAY_NOTIFICATION_TIME);
    }

    /**
     * not retry register when register throw exception.
     * @param metadata register information
     */
    default void reExportRegister(Metadata metadata) {
        register(metadata);
    }

    /**
     * not retry unregister when unregister throw exception.
     * @param metadata unregister information
     */
    default void reExportUnregister(Metadata metadata) {
        unregister(metadata);
    }



}
