package com.hqy.cloud.alarm.notification.core.email;

import cn.hutool.core.collection.CollectionUtil;
import com.hqy.cloud.alarm.notification.api.AbstractNotificationController;
import com.hqy.cloud.alarm.notification.common.NoticeTarget;
import com.hqy.cloud.alarm.notification.common.NotificationType;
import com.hqy.cloud.alarm.notification.config.EmailNotifierConfigProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * 邮件通知控制器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/19
 */
@Slf4j
public class EmailNotificationController extends AbstractNotificationController {
    private final EmailNotifierConfigProperties properties;

    public EmailNotificationController(EmailNotifierConfigProperties properties) {
        this.properties = properties;
    }

    @Override
    public NotificationType notificationType() {
        return NotificationType.EMAIL;
    }

    @Override
    public List<NoticeTarget> target() {
        if (!properties.isEnabled()) {
            return List.of();
        }
        // 获取通知人
        List<String> targets = properties.getTargets();
        if (CollectionUtil.isEmpty(targets)) {
            log.warn("Not found email notification targets!");
        }
        return targets.stream().map(NoticeTarget::of).toList();
    }

}