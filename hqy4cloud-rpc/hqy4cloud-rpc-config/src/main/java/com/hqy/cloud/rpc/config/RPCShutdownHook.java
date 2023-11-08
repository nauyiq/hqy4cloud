package com.hqy.cloud.rpc.config;

import com.hqy.cloud.rpc.model.ApplicationModel;
import com.hqy.cloud.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RPCShutdownHook.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/31 16:50
 */
public class RPCShutdownHook extends Thread {
    private final static Logger log = LoggerFactory.getLogger(RPCShutdownHook.class);

    private final ShutdownHookCallbacks callbacks = new ShutdownHookCallbacks();
    private final ApplicationModel applicationModel;

    public RPCShutdownHook(ApplicationModel applicationModel) {
        super("RPCShutdownHook");
        this.applicationModel = applicationModel;
        AssertUtil.notNull(this.applicationModel, "ApplicationModel should not be null.");
    }

    @Override
    public void run() {
        if (destroyed.compareAndSet(false, true)) {
            if (log.isInfoEnabled()) {
                log.info("Run shutdown hook now.");
            }
            doDestroy();
        }
    }

    private void doDestroy() {
        applicationModel.destroy();
    }

    public void register() {
        if (registered.compareAndSet(false, true)) {
            try {
                Runtime.getRuntime().addShutdownHook(this);
            } catch (IllegalStateException e) {
                log.warn("register shutdown hook failed: " + e.getMessage());
            } catch (Exception e) {
                log.warn("register shutdown hook failed: " + e.getMessage(), e);
            }
        }
    }

    public void unregister() {
        if (registered.compareAndSet(true, false)) {
            if (this.isAlive()) {
                // RPCShutdownHook thread is running
                return;
            }
            try {
                Runtime.getRuntime().removeShutdownHook(this);
            } catch (IllegalStateException e) {
                log.warn("unregister shutdown hook failed: " + e.getMessage());
            } catch (Exception e) {
                log.warn("unregister shutdown hook failed: " + e.getMessage(), e);
            }
        }
    }

    public boolean getRegistered() {
        return registered.get();
    }

    /**
     * Has it already been registered or not?
     */
    private final AtomicBoolean registered = new AtomicBoolean(false);

    /**
     * Has it already been destroyed or not?
     */
    private final AtomicBoolean destroyed = new AtomicBoolean(false);


    public ShutdownHookCallbacks getCallbacks() {
        return callbacks;
    }
}
