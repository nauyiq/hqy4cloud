package com.hqy.socketio.support;

import com.hqy.socketio.ServerLauncherFactory;

import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/23 14:28
 */
public class ServerEventLauncherFactory implements ServerLauncherFactory {

    private final Set<EventListener> eventListeners;

    public ServerEventLauncherFactory(Set<EventListener> eventListeners) {
        this.eventListeners = eventListeners;
    }

    @Override
    public ServerEventLauncher create() {
        return new ServerEventLauncher(false, true, "", "", eventListeners);
    }


}
