package com.hqy.cloud.alarm.notification.api;

import com.hqy.cloud.alarm.notification.core.NotificationHolder;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/20 10:56
 */
public abstract class AbstractNotificationController implements NotificationController {

    @Override
    public Notifier notifier() {
        return NotificationHolder.getNotifier(notificationType());
    }
}
