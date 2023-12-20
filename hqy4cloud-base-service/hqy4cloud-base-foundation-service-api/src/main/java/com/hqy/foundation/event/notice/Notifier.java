package com.hqy.foundation.event.notice;

import com.hqy.foundation.common.EventContent;
import com.hqy.foundation.common.EventType;

/**
 * 通知器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/19 15:11
 */
public interface Notifier {

    /**
     * 通知器类型
     * @return {@link NotificationType}
     */
    NotificationType type();

    /**
     * 进行业务通知
     * @param eventType 事件类型
     * @param content   通知内容
     * @param config    通知配置类
     */
    <T extends EventContent> void notify(EventType eventType, T content, NotificationController config);

}
