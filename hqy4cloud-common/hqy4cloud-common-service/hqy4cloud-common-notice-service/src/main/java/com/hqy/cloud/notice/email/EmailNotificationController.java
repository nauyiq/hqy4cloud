package com.hqy.cloud.notice.email;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.foundation.event.alerter.AbstractNotificationController;
import com.hqy.foundation.common.EventType;
import com.hqy.foundation.event.notice.NoticeTarget;
import com.hqy.foundation.event.notice.NotificationType;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.stream.Collectors;

import static com.hqy.cloud.notice.NoticeConstants.DEFAULT_SYSTEM_EMAIL;
import static com.hqy.cloud.notice.NoticeConstants.SYSTEM_EMAIL_TO_PREFIX;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/19
 */
public class EmailNotificationController extends AbstractNotificationController {

    @Override
    public NotificationType notificationType() {
        return NotificationType.EMAIL;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<NoticeTarget> target(Environment environment, EventType eventType) {
        String key = SYSTEM_EMAIL_TO_PREFIX + StrUtil.DOT + eventType.name;
        List<String> emails = environment.getProperty(key, List.class, List.of(DEFAULT_SYSTEM_EMAIL));
        return emails.stream().map(NoticeTarget::of).toList();
    }

}