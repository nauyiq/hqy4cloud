package com.hqy.rpc.registry.api;

import com.hqy.rpc.registry.client.RegistryService;
import com.hqy.rpc.registry.node.NodeService;

import static com.hqy.rpc.common.CommonConstants.*;

/**
 * 注册中心
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 16:24
 */
public interface Registry extends RegistryService, NodeService {


    /**
     * 获取延迟通知时间
     * @return 延迟通知时间
     */
    default int getNotifyDelay() {
        return getUrl().getParameter(REGISTRY_DELAY_NOTIFICATION_KEY, DEFAULT_DELAY_NOTIFICATION_TIME);
    }




}
