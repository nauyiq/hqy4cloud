package com.hqy.foundation.notice;

import com.hqy.foundation.common.EventType;

/**
 * 通知器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/19 15:11
 */
public interface Notifier {

    /**
     * 进行业务通知
     * @param eventType 事件类型
     * @param content   通知内容
     * @param config    通知配置类
     */
    <T> void notify(EventType eventType, T content, NotificationConfig config);

}
