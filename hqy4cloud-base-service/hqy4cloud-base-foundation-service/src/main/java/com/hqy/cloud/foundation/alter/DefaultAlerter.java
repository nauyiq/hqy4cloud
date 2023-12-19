package com.hqy.cloud.foundation.alter;

import com.hqy.cloud.util.AssertUtil;
import com.hqy.foundation.common.EventType;
import com.hqy.foundation.notice.Notifier;
import com.hqy.foundation.notice.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

/**
 * 业务通知的全局入口
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/15 17:33
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultAlerter implements Alerter {
    private final Environment environment;

    @Override
    public <T> void notify(EventType eventType, NotificationType type, T content) {
        AssertUtil.notNull(type, "Notification type should not be null.");
        AssertUtil.notNull(content, "Alerter notification content should not be null.");
        // 获取通知配置.
        NotificationConfig notificationConfig = NotificationHolder.getNotification(type);
        if (notificationConfig == null) {
            log.warn("Not found notification config by type:{}.", type);
            return;
        }
        Notifier notifier = notificationConfig.notifier(environment);
        try {
            // 进行业务通知
            notifier.notify(eventType, content, notificationConfig);
        } catch (Throwable cause) {
            log.error("Failed execute notify by {} notifier, cause: {}.", type, cause.getMessage(), cause);
        }
    }

}
