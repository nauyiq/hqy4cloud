package com.hqy.foundation.notice;

import com.hqy.foundation.common.EventType;

/**
 * 报警器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/15 16:12
 */
public interface Alerter {

    /**
     * 发通知
     * @param eventType  事件类型
     * @param type       通知的类型
     * @param content    通知的内容
     */
    <T> void notify(EventType eventType, NotificationType type, T content);

}
