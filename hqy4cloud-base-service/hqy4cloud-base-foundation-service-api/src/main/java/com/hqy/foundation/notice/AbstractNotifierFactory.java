package com.hqy.foundation.notice;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/19 17:27
 */
@Slf4j
public abstract class AbstractNotifierFactory implements NotifierFactory {
    private final static Map<NotificationType, Notifier> NOTIFIER_MAP = MapUtil.newConcurrentHashMap();

    @Override
    public Notifier create(NotificationType type, Environment environment) {
        log.info("Create notifier by {} factory", type);
        return NOTIFIER_MAP.computeIfAbsent(type, value -> doCreate(environment));
    }

    /**
     * 创建通知器
     * @param environment {@link Environment}
     * @return            {@link Notifier}
     */
    protected abstract Notifier doCreate(Environment environment);
}
