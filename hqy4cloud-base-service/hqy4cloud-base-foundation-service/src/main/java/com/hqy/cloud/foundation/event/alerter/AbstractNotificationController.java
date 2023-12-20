package com.hqy.cloud.foundation.event.alerter;

import com.hqy.foundation.event.notice.NotificationController;
import com.hqy.foundation.event.notice.Notifier;

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
