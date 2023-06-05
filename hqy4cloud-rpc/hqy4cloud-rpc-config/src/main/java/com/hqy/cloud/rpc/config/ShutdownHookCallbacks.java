package com.hqy.cloud.rpc.config;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import static com.hqy.foundation.common.thread.ThrowableAction.execute;

/**
 * The composed {@link ShutdownHookCallback} class to manipulate one and more {@link ShutdownHookCallback} instances
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/1 17:35
 */
public class ShutdownHookCallbacks {
    private final List<ShutdownHookCallback> callbacks = new LinkedList<>();


    public ShutdownHookCallbacks() {
        loadCallbacks();
    }

    private void loadCallbacks() {
        ServiceLoader<ShutdownHookCallback> hookCallbacks = ServiceLoader.load(ShutdownHookCallback.class);
        hookCallbacks.forEach(callbacks::add);
    }

    public ShutdownHookCallbacks addCallback(ShutdownHookCallback callback) {
        synchronized (this) {
            this.callbacks.add(callback);
        }
        return this;
    }

    public Collection<ShutdownHookCallback> getCallbacks() {
        synchronized (this) {
            return this.callbacks;
        }
    }

    public void clear() {
        synchronized (this) {
            callbacks.clear();
        }
    }

    public void callback() {
        getCallbacks().forEach(callback -> execute(callback::callback));
    }




}
