package com.hqy.cloud.actuator.server;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.actuator.core.GradeSwitcherListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/20 11:18
 */
@Slf4j
public enum GradeSwitcherListenerRepository {

    /**
     * 单例对象实例
     */
    INSTANCE;

    public static final Map<Integer, GradeSwitcherListener> MAP = MapUtil.newConcurrentHashMap(8);
    private final static GradeSwitcherListener DEFAULT_LISTENER = new DefaultGradeSwitcherListener();

    public static GradeSwitcherListenerRepository getInstance() {
        return INSTANCE;
    }

    public GradeSwitcherListener queryListener(Integer switcherId) {
        if (switcherId == null) {
            log.warn("Switcher id should not be null.");
            return null;
        }
        return MAP.getOrDefault(switcherId, DEFAULT_LISTENER);
    }

    public void registryListener(Integer switcherId, GradeSwitcherListener listener) {
        if (switcherId == null || listener == null) {
            log.warn("Failed execute to registry listener, because switcherId or listener is null.");
            return;
        }
        if (MAP.containsKey(switcherId)) {
            log.warn("Switcher already bind listener, id = {}.", switcherId);
        } else {
            MAP.put(switcherId, listener);
        }
    }



}
