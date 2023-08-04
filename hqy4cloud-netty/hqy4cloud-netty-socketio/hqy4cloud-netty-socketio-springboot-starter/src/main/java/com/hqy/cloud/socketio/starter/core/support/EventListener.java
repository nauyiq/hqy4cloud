package com.hqy.cloud.socketio.starter.core.support;

import com.corundumstudio.socketio.listener.DataListener;
import com.google.common.base.Objects;
import lombok.RequiredArgsConstructor;

/**
 * EventListener.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/23 13:34
 */
@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
public class EventListener {
    private final String eventName;
    private final Class<?> eventClass;
    private final DataListener<?> dataListener;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventListener that = (EventListener) o;
        return Objects.equal(eventName, that.eventName) && Objects.equal(eventClass, that.eventClass) && Objects.equal(dataListener, that.dataListener);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(eventName, eventClass, dataListener);
    }

    public String getEventName() {
        return eventName;
    }

    public Class getEventClass() {
        return eventClass;
    }

    public DataListener getDataListener() {
        return dataListener;
    }
}
