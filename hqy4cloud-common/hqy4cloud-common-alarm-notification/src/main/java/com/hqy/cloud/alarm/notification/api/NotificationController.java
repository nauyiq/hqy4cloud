package com.hqy.cloud.alarm.notification.api;

import com.hqy.cloud.alarm.notification.common.EventType;
import com.hqy.cloud.alarm.notification.common.NotificationType;

import java.util.List;

/**
 * 通知控制器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/15 16:14
 */
public interface NotificationController {

    /**
     * 获取通知的类型
     * @return 返回当前模板的通知类型 {@link NotificationType}
     */
    NotificationType notificationType();

    /**
     * 通知目标
     * @param environment {@link Environment}
     * @param eventType   事件类型
     * @return 获取通知的对象
     */
    List<NoticeTarget> target(Environment environment, EventType eventType);

    /**
     * 获取通知器
     * @return {@link Notifier}
     */
    Notifier notifier();









}
