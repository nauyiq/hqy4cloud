package com.hqy.cloud.communication.sms.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/12
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = SmsProperties.PREFIX)
public class SmsProperties {
    public static final String PREFIX = "hqy4cloud.sms";

    private boolean enabled = true;

    private String host;

    private String path;

    private String appcode;

    private Map<String, SmsTemplate> templates = new HashMap<>();


    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SmsTemplate {

        /**
         * 模板名称
         */
        private String name;

        /**
         * 短信模板ID
         */
        private String templateId;

        /**
         * 短信签名ID
         */
        private String smsSignId;



    }


}
