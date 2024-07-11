package com.hqy.cloud.alarm.notification.core;

import cn.hutool.extra.spring.SpringUtil;
import com.hqy.cloud.alarm.notification.api.Alerter;
import com.hqy.cloud.alarm.notification.api.NotificationContent;
import com.hqy.cloud.alarm.notification.common.NotificationType;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务通知的全局入口
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/20
 */
@Slf4j
public record AlerterHolder(Alerter alerter) {

    public static AlerterHolder getInstance() {
        return SpringUtil.getBean(AlerterHolder.class);
    }

    public <T extends NotificationContent> void notify(NotificationType type, T content) {
        alerter.notify(type, content);
    }

}
