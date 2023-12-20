package com.hqy.cloud.foundation.event.alerter;

import com.hqy.cloud.util.spring.SpringContextHolder;
import com.hqy.foundation.common.EventContent;
import com.hqy.foundation.common.EventType;
import com.hqy.foundation.event.notice.Alerter;
import com.hqy.foundation.event.notice.NotificationType;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务通知的全局入口
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/20 9:56
 */
@Slf4j
public record AlerterHolder(Alerter alerter) {

    public static AlerterHolder getInstance() {
        return SpringContextHolder.getBean(AlerterHolder.class);
    }

    public <T extends EventContent> void notify(EventType eventType, NotificationType type, T content) {
        alerter.notify(eventType, type, content);
    }

    public Alerter getAlerter() {
        return alerter;
    }
}
