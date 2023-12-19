package com.hqy.cloud.foundation.alter;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.foundation.spi.SpiInstanceServiceLoad;
import com.hqy.foundation.notice.NotificationConfig;
import com.hqy.foundation.notice.NotificationType;

import java.util.Map;

/**
 * 通知配置上下文
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/15 17:38
 */
public class NotificationHolder {
    private final static Map<NotificationType, NotificationConfig> NOTIFICATION_CONFIG_MAP = MapUtil.newConcurrentHashMap();

    static {
        SpiInstanceServiceLoad.register(NotificationConfig.class);
        for (NotificationConfig serviceInstance : SpiInstanceServiceLoad.getServiceInstances(NotificationConfig.class)) {
            NOTIFICATION_CONFIG_MAP.put(serviceInstance.notificationType(), serviceInstance);
        }
    }

    /**
     * 通过通知类型获取通知对象
     * @param type 通知类型
     * @return     通知对象，配置
     */
    public static NotificationConfig getNotification(NotificationType type) {
        return NOTIFICATION_CONFIG_MAP.get(type);
    }





}
