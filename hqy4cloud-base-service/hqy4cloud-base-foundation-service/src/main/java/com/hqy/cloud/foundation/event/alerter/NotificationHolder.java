package com.hqy.cloud.foundation.event.alerter;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.util.spi.SpiInstanceServiceLoad;
import com.hqy.foundation.event.notice.NotificationController;
import com.hqy.foundation.event.notice.NotificationType;
import com.hqy.foundation.event.notice.Notifier;

import java.util.Map;

/**
 * 通知配置上下文
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/15 17:38
 */
public class NotificationHolder {
    private final static Map<NotificationType, NotificationController> NOTIFICATION_CONFIG_MAP = MapUtil.newConcurrentHashMap();
    private final static Map<NotificationType, Notifier> NOTIFIER_MAP = MapUtil.newConcurrentHashMap();

    static {
        SpiInstanceServiceLoad.register(NotificationController.class);
        for (NotificationController serviceInstance : SpiInstanceServiceLoad.getServiceInstances(NotificationController.class)) {
            NOTIFICATION_CONFIG_MAP.put(serviceInstance.notificationType(), serviceInstance);
        }
    }

    public static void registryNotifier(NotificationType type, Notifier notifier) {
        NOTIFIER_MAP.put(type, notifier);
    }

    public static Notifier getNotifier(NotificationType type) {
        return NOTIFIER_MAP.get(type);
    }

    /**
     * 通过通知类型获取通知对象
     * @param type 通知类型
     * @return     通知对象，配置
     */
    public static NotificationController getNotification(NotificationType type) {
        return NOTIFICATION_CONFIG_MAP.get(type);
    }






}
