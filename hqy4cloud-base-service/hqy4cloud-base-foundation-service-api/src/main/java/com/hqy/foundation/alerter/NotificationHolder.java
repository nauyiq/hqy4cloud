package com.hqy.foundation.alerter;

/**
 * 通知配置上下文
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/15 17:38
 */
public interface NotificationHolder {

    /**
     * 通过通知类型获取通知对象
     * @param type 通知类型
     * @return     通知对象，配置
     */
    NotificationConfig getNotification(NotificationType type);


}
