package com.hqy.cloud.communication.request;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/12
 */
public enum SmsType {

    /**
     * 短信认证, 用于认证登录
     */
    SMS_AUTH("auth"),



    ;

    public final String type;



    SmsType(String type) {
        this.type = type;
    }
}
