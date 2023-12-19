package com.hqy.cloud.notice.email;

import com.hqy.foundation.common.EventType;
import com.hqy.foundation.notice.NoticeTarget;
import com.hqy.foundation.notice.NotificationConfig;
import com.hqy.foundation.notice.NotificationType;
import com.hqy.foundation.notice.Notifier;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.stream.Collectors;

import static com.hqy.cloud.notice.NoticeConstants.DEFAULT_SYSTEM_EMAIL;
import static com.hqy.cloud.notice.NoticeConstants.SYSTEM_EMAIL_TO_PREFIX;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/19 17:10
 */
public class EmailNotificationConfig implements NotificationConfig {

    @Override
    public NotificationType notificationType() {
        return NotificationType.EMAIL;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<NoticeTarget> target(Environment environment, EventType eventType) {
        String key = SYSTEM_EMAIL_TO_PREFIX + eventType.name;
        List<String> emails = environment.getProperty(key, List.class, List.of(DEFAULT_SYSTEM_EMAIL));
        return emails.stream().map(NoticeTarget::of).collect(Collectors.toList());
    }

    @Override
    public Notifier notifier(Environment environment) {
        return EmailNotifierFactory.getInstance().doCreate(environment);
    }
}
