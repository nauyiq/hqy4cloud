package com.hqy.cloud.rpc.config.deploy;

import com.hqy.cloud.rpc.config.RPCShutdownHook;
import com.hqy.cloud.rpc.deploy.AbstractDeployer;
import com.hqy.cloud.rpc.deploy.ApplicationDeployer;
import com.hqy.cloud.rpc.model.ApplicationModel;
import com.hqy.cloud.rpc.registry.api.support.RegistryManager;
import com.hqy.cloud.rpc.threadpool.DefaultExecutorRepository;
import com.hqy.cloud.rpc.threadpool.ExecutorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * initialize and start application instance.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/31 17:49
 */
public class DefaultApplicationDeployer extends AbstractDeployer<ApplicationModel> implements ApplicationDeployer {
    private final static Logger log = LoggerFactory.getLogger(DefaultApplicationDeployer.class);

    private final ApplicationModel applicationModel;
    private final RPCShutdownHook shutdownHook;
    private final ExecutorRepository executorRepository;
    private final Object destroyLock = new Object();
    private final Object startLock = new Object();
    private volatile CompletableFuture<Boolean> startFuture;


    public DefaultApplicationDeployer(ApplicationModel model) {
        super(model);
        this.applicationModel = model;
        this.shutdownHook = new RPCShutdownHook(model);
        this.executorRepository = new DefaultExecutorRepository(model.getSelfModel());
    }


    @Override
    public void initialize() throws IllegalStateException {
        if (initialized) {
            return;
        }
        // register shutdown hook
        registerShutdownHook();

        // init application model
        applicationModel.initialize();

        initialized = true;
        if (log.isInfoEnabled()) {
            log.info(getIdentifier() + " has been initialized.");
        }
    }

    private void registerShutdownHook() {
        shutdownHook.register();
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

                initialize();

                onStarted();
            } catch (Throwable cause) {
                onFailed(getIdentifier() + " start failure", cause);
                throw cause;
            }

            return startFuture;
        }
    }


    protected void onStarted() {
        try {
            // starting -> started
            if (!isStarting()) {
                return;
            }
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

    @Override
    public void stop() throws IllegalStateException {
        applicationModel.destroy();
    }

    @Override
    public ApplicationModel getModel() {
        return applicationModel;
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

    private void unRegisterShutdownHook() {
        shutdownHook.unregister();
    }

    private void destroyRegistries() {
        RegistryManager.getInstance().destroyAll();
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

    @Override
    public void postDestroy() {
        synchronized (destroyLock) {
            // expect application model is destroyed before here
            if (isStopped()) {
                return;
            }
            try {
                executeShutdownCallbacks();
                // destroy all executor services
                destroyExecutorRepository();

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

    private void destroyExecutorRepository() {
        executorRepository.destroyAll();
    }

    private void executeShutdownCallbacks() {
        this.shutdownHook.getCallbacks().callback();
    }

    @Override
    public ExecutorRepository getExecutorRepository() {
        return executorRepository;
    }
}
