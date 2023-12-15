package com.hqy.foundation.alerter;

/**
 * 业务发通知模板
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/15 16:14
 */
public interface NotificationConfig {

    /**
     * 获取通知的类型
     * @return 返回当前模板的通知类型 {@link NotificationType}
     */
    NotificationType notificationType();

    /**
     * 获取需要通知的人
     * @return 获取通知的对象， 多个通知人或者value时 用 `,` 隔开
     */
    String notifier();

/*
    */
/**
     * 获取通知的模板
     * @return 通知的模板
     *//*

    String template();
*/

    /**
     * 获取报警内容工厂类
     * @return 报警内容工厂类
     */
    <T, V> AlerterContentFactory<T, V> contentFactory();









}
