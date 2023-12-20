package com.hqy.cloud.notice.email;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.hqy.cloud.notice.NoticeConstants.DEFAULT_SYSTEM_EMAIL;
import static com.hqy.cloud.notice.NoticeConstants.SYSTEM_EMAIL_TO_PREFIX;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/20 11:18
 */
@Data
@ConfigurationProperties(prefix = SYSTEM_EMAIL_TO_PREFIX)
public class EmailNotifierConfig {

    private boolean enabled = true;
    private long rateMillis = 60 * 1000;
    private String sender = DEFAULT_SYSTEM_EMAIL;

}
