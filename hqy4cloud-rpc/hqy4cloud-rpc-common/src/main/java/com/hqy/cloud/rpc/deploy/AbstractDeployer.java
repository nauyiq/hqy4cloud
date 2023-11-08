package com.hqy.cloud.rpc.deploy;

import com.hqy.cloud.rpc.model.ScopeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.hqy.cloud.rpc.deploy.DeployState.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/31 17:25
 */
public abstract class AbstractDeployer<T extends ScopeModel> implements Deployer<T> {
    private static final Logger log = LoggerFactory.getLogger(AbstractDeployer.class);

    private volatile DeployState state = PENDING;
    private volatile Throwable lastError;
    protected volatile boolean initialized = false;
    protected List<DeployListener<T>> listeners = new ArrayList<>();
    private final T model;
    public AbstractDeployer(T model) {
        this.model = model;
    }

    @Override
    public boolean isPending() {
        return state == PENDING;
    }

    @Override
    public boolean isRunning() {
        return state == STARTING || state == STARTED;
    }

    @Override
    public boolean isStarted() {
        return state == STARTED;
    }

    @Override
    public boolean isStarting() {
        return state == STARTING;
    }

    @Override
    public boolean isStopping() {
        return state == STOPPING;
    }

    @Override
    public boolean isStopped() {
        return state == STOPPED;
    }

    @Override
    public boolean isFailed() {
        return state == FAILED;
    }

    @Override
    public DeployState getState() {
        return state;
    }

    @Override
    public void addDeployListener(DeployListener<T> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeDeployListener(DeployListener<T> listener) {
        listeners.remove(listener);
    }

    public void setPending() {
        this.state = PENDING;
    }

    protected void setStarting() {
        this.state = STARTING;
        for (DeployListener<T> listener : listeners) {
            try {
                listener.onStarting(model);
            } catch (Throwable e) {
                log.error(getIdentifier() + " an exception occurred when handle starting event", e);
            }
        }
    }

    protected void setStarted() {
        this.state = STARTED;
        for (DeployListener<T> listener : listeners) {
            try {
                listener.onStarted(model);
            } catch (Throwable e) {
                log.error(getIdentifier() + " an exception occurred when handle started event", e);
            }
        }
    }
    protected void setStopping() {
        this.state = STOPPING;
        for (DeployListener<T> listener : listeners) {
            try {
                listener.onStopping(model);
            } catch (Throwable e) {
                log.error(getIdentifier() + " an exception occurred when handle stopping event", e);
            }
        }
    }

    protected void setStopped() {
        this.state = STOPPED;
        for (DeployListener<T> listener : listeners) {
            try {
                listener.onStopped(model);
            } catch (Throwable e) {
                log.error(getIdentifier() + " an exception occurred when handle stopped event", e);
            }
        }
    }

    protected void setFailed(Throwable error) {
        this.state = FAILED;
        this.lastError = error;
        for (DeployListener<T> listener : listeners) {
            try {
                listener.onFailure(model, error);
            } catch (Throwable e) {
                log.error(getIdentifier() + " an exception occurred when handle failed event", e);
            }
        }
    }

    @Override
    public Throwable getError() {
        return lastError;
    }

    public boolean isInitialized() {
        return initialized;
    }

    protected String getIdentifier() {
        return this.model.getClass().getSimpleName();
    }





}
