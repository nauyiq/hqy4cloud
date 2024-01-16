package com.hqy.cloud.registry.deploy;

import com.hqy.cloud.registry.api.support.RegistryManager;
import com.hqy.cloud.registry.common.deploy.AbstractDeployer;
import com.hqy.cloud.registry.common.deploy.DestroyDeployerService;
import com.hqy.cloud.registry.hook.ShutdownHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * ApplicationLifecycleDeployer.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/3
 */
public class ApplicationLifecycleDeployer extends AbstractDeployer<ApplicationDeployModel> implements DestroyDeployerService {
    private static final Logger log = LoggerFactory.getLogger(ApplicationLifecycleDeployer.class);

    private final ApplicationDeployModel deployModel;
    private final ShutdownHook shutdownHook;
    private final Object destroyLock = new Object();
    private final Object startLock = new Object();
    private volatile CompletableFuture<Boolean> startFuture;

    public ApplicationLifecycleDeployer(ApplicationDeployModel model) {
        super(model);
        this.deployModel = model;
        this.shutdownHook = new ShutdownHook(model);
    }

    @Override
    public void initialize() throws IllegalStateException {
        if (initialized) {
            return;
        }
        // register shutdown hook
        registerShutdownHook();

        // init Lifecycle model
        deployModel.initialize();
        initialized = true;
        if (log.isInfoEnabled()) {
            log.info(getIdentifier() + " has been initialized.");
        }
    }

    private void registerShutdownHook() {
        shutdownHook.register();
    }

    private void unRegisterShutdownHook() {
        shutdownHook.unregister();
    }

    @Override
    public Future<?> start() throws IllegalStateException {
        synchronized (startLock) {
            if (isStopping() || isStopped() || isFailed() || isStarting()) {
                throw new IllegalStateException(getIdentifier() + " is starting|stopping|stopped, can not start again");
            }
            try {
                // pending -> starting : first start app
                // started -> starting : re-start app
                onStarting();

//                initialize();

                onStarted();
            } catch (Throwable cause) {
                onFailed(getIdentifier() + " start failure", cause);
                throw cause;
            }
            return startFuture;
        }
    }

    private void onStarting() {
        // pending -> starting
        // started -> starting
        if (!(isPending() || isStarted())) {
            return;
        }
        setStarting();
        startFuture = new CompletableFuture();
        if (log.isInfoEnabled()) {
            log.info(getIdentifier() + " is starting.");
        }
    }

    protected void onStarted() {
        try {
            // starting -> started
            if (!isStarting()) {
                return;
            }
            // start lifecycle
            deployModel.getRegistryContext().start();
            // setting started status.
            setStarted();
            if (log.isInfoEnabled()) {
                log.info(getIdentifier() + " is ready.");
            }
        } finally {
            // complete future
            completeStartFuture(true);
        }
    }

    private void onFailed(String msg, Throwable cause) {
        try {
            setFailed(cause);
            log.error(msg, cause);
        } finally {
            completeStartFuture(false);
        }
    }

    private void completeStartFuture(boolean success) {
        if (startFuture != null) {
            startFuture.complete(success);
        }
    }



    @Override
    public void stop() throws IllegalStateException {
        deployModel.destroy();
    }

    @Override
    public void preDestroy() {
        synchronized (destroyLock) {
            if (isStopping() || isStopped()) {
                return;
            }
            onStopping();

            destroyRegistries();

            unRegisterShutdownHook();
        }
    }

    private void onStopping() {
        try {
            if (isStopping() || isStopped()) {
                return;
            }
            setStopping();
            if (log.isInfoEnabled()) {
                log.info(getIdentifier() + " is stopping.");
            }
        } finally {
            completeStartFuture(false);
        }
    }

    private void destroyRegistries() {
        RegistryManager.getInstance().destroyAll();
    }

    @Override
    public void postDestroy() {
        synchronized (destroyLock) {
            // expect application model is destroyed before here
            if (isStopped()) {
                return;
            }
            try {
                executeShutdownCallbacks();

                onStopped();
            } catch (Throwable ex) {
                String msg = getIdentifier() + " an error occurred while stopping application: " + ex.getMessage();
                onFailed(msg, ex);
            }
        }
    }

    private void onStopped() {
        try {
            if (isStopped()) {
                return;
            }
            setStopped();
            if (log.isInfoEnabled()) {
                log.info(getIdentifier() + " has stopped.");
            }
        } finally {
            completeStartFuture(false);
        }
    }

    private void executeShutdownCallbacks() {
        this.shutdownHook.getCallbacks().callback();
    }
}
