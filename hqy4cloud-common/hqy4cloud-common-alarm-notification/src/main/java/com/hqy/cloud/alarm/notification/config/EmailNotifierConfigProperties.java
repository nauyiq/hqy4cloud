package com.hqy.cloud.alarm.notification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.hqy.cloud.alarm.notification.common.NotificationConstants.DEFAULT_SYSTEM_EMAIL;
import static com.hqy.cloud.alarm.notification.common.NotificationConstants.SYSTEM_EMAIL_TO_PREFIX;


/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/20 11:18
 */
@Getter
@Setter
@ConfigurationProperties(prefix = SYSTEM_EMAIL_TO_PREFIX)
public class EmailNotifierConfigProperties extends BusinessNotificationProperties {

    /**
     * 发送人邮件地址
     */
    private String sender = DEFAULT_SYSTEM_EMAIL;


}
