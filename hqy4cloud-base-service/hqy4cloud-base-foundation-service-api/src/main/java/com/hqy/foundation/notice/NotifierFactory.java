package com.hqy.foundation.notice;

import org.springframework.core.env.Environment;

/**
 * 通知器配置工厂类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/15 17:27
 */
public interface NotifierFactory {

    /**
     * 创建业务报警内容对象
     * @param type          通知类型
     * @param environment   {@link Environment}
     * @return              对应的通知器
     */
    Notifier create(NotificationType type, Environment environment);

}
