package com.hqy.cloud.communication.sms.core;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/12
 */
public interface SmsSender {

    /**
     * 发消息
     * @param type        消息类型
     * @param phoneNumber 手机号码
     * @param code        短信验证码
     * @return            是否发送成功
     */
    boolean send(String type, String phoneNumber, String code);
}
