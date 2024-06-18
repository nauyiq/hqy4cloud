package com.hqy.cloud.alarm.notification.api;

import com.hqy.cloud.alarm.notification.common.NotificationType;

/**
 * 报警器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/15 16:12
 */
public interface Alerter {

    /**
     * 发通知
     * @param type       通知的类型
     * @param content    通知的内容
     */
    <T extends NotificationContent> void notify(NotificationType type, T content);



}
