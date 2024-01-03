package com.hqy.cloud.registry.hook;

import com.hqy.cloud.registry.deploy.LifecycleDeployModel;
import com.hqy.cloud.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ShutdownHook.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public class ShutdownHook extends Thread {
    private static final Logger log = LoggerFactory.getLogger(ShutdownHook.class);

    private final LifecycleDeployModel model;
    private final ShutdownHookCallbacks callbacks = new ShutdownHookCallbacks();

    public ShutdownHook(LifecycleDeployModel model) {
        super(ShutdownHook.class.getSimpleName());
        this.model = model;
        AssertUtil.notNull(this.model, "Application model should not be null.");
    }

    /**
     * Has it already been registered or not?
     */
    private final AtomicBoolean registered = new AtomicBoolean(false);

    /**
     * Has it already been destroyed or not?
     */
    private final AtomicBoolean destroyed = new AtomicBoolean(false);


    @Override
    public void run() {
        if (destroyed.compareAndSet(false, true)) {
            if (log.isInfoEnabled()) {
                log.info("Run shutdown hook now.");
            }
            doDestroy();
        }
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

    private void doDestroy() {
        model.destroy();
    }

    public ShutdownHookCallbacks getCallbacks() {
        return callbacks;
    }
}
