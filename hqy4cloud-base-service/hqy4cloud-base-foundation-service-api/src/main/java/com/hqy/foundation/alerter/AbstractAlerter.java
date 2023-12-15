package com.hqy.foundation.alerter;

import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务报警器基类, 封装了业务报警的流程 或者模板
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/15 17:33
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractAlerter implements Alerter {
    private final NotificationHolder notificationHolder;


    @Override
    public <T> void notify(NotificationType type, T content) {
        AssertUtil.notNull(type, "Notification type should not be null.");
        AssertUtil.notNull(content, "Alerter notification content should not be null.");
        //1. 获取通知配置.
        NotificationConfig notificationConfig = notificationHolder.getNotification(type);
        if (notificationConfig == null) {
            log.warn("Not found notification config by type:{}.", type);
            return;
        }
        // 2. 构造报警内容工厂
        AlerterContentFactory<T, Object> factory = notificationConfig.contentFactory();
        Object notificationContent = factory.create(content);
        // 3. 执行通知
        doNotify(notificationConfig.notifier(), notificationContent);
    }

    /**
     * 执行真正通知
     * @param notifier            通知者
     * @param notificationContent 通知内容
     */
    protected abstract void doNotify(String notifier, Object notificationContent);


}
