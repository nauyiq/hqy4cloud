package com.hqy.cloud.alarm.notification.core.email;

import com.hqy.cloud.alarm.notification.api.NotificationContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailContent implements NotificationContent {

    private String id;
    private String sender;
    private String subject;
    private String content;

    @Override
    public String scene() {
        return id;
    }
}
