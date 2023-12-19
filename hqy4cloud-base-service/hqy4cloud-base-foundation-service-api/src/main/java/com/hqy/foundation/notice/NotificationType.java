package com.hqy.foundation.notice;

/**
 * 通知类型
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/15 16:15
 */
public enum NotificationType {

    /**
     * 短信通知
     */
    SMS,

    /**
     * 邮箱通知
     */
    EMAIL,

    /**
     * 企业微信通知 （机器人等....）
     */
    ENTERPRISE_WECHAT,

    /**
     * 钉钉通知
     */
    DING_DING


}
