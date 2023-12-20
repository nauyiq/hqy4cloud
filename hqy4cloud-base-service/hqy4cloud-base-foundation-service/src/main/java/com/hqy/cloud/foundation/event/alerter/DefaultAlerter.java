package com.hqy.cloud.foundation.event.alerter;

import com.hqy.cloud.util.AssertUtil;
import com.hqy.foundation.common.EventContent;
import com.hqy.foundation.common.EventType;
import com.hqy.foundation.event.notice.Alerter;
import com.hqy.foundation.event.notice.NotificationController;
import com.hqy.foundation.event.notice.NotificationType;
import com.hqy.foundation.event.notice.Notifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/15 17:33
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultAlerter implements Alerter {

    @Override
    public <T extends EventContent> void notify(EventType eventType, NotificationType type, T content) {
        AssertUtil.notNull(type, "Notification type should not be null.");
        AssertUtil.notNull(content, "Alerter notification content should not be null.");
        // 获取通知配置.
        NotificationController notificationController = NotificationHolder.getNotification(type);
        if (notificationController == null) {
            log.warn("Not found notification config by type:{}.", type);
            return;
        }
        Notifier notifier = notificationController.notifier();
        if (notifier == null) {
            log.error("Notifier not found by {}.", type);
            return;
        }
        try {
            // 进行业务通知
            notifier.notify(eventType, content, notificationController);
        } catch (Throwable cause) {
            log.error("Failed execute notify by {} notifier, cause: {}.", type, cause.getMessage(), cause);
        }
    }

}
