package com.hqy.cloud.rpc.deploy;

import com.hqy.cloud.rpc.model.ScopeModel;

import java.util.concurrent.Future;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/31 17:07
 */
public interface Deployer<T extends ScopeModel> {

    /**
     * Initialize the component
     * @throws IllegalStateException e
     */
    void initialize() throws IllegalStateException;

    /**
     * Stops the component.
     * @return Future
     * @throws IllegalStateException e
     */
    Future<?> start() throws IllegalStateException;


    /**
     * Stops the component.
     * @throws IllegalStateException e
     */
    void stop() throws IllegalStateException;

    /**
     * true if the component is added and waiting to start
     * @return result.
     */
    boolean isPending();

    /**
     * true if the component is starting or has been started.
     * @return result.
     */
    boolean isRunning();

    /**
     * true if the component is starting.
     * @return result.
     * @see #isStarted()
     */
    boolean isStarting();

    /**
     * true if the component has been started.
     * @return result.
     * @see #start()
     * @see #isStarting()
     */
    boolean isStarted();

    /**
     * true if the component is stopping.
     * @return result.
     * @see #isStopped()
     */
    boolean isStopping();

    /**
     * true if the component is stopping.
     * @return result.
     * @see #isStopped()
     */
    boolean isStopped();

    /**
     * true if the component has failed to start or has failed to stop.
     * @return result.
     */
    boolean isFailed();

    /**
     * current state
     * @return {@link DeployState}
     */
    DeployState getState();

    /**
     * add deploy listener.
     * @param listener {@link DeployListener}
     */
    void addDeployListener(DeployListener<T> listener);

    /**
     * remove deploy listener.
     * @param listener {@link DeployListener}
     */
    void removeDeployListener(DeployListener<T> listener);

    /**
     * return error
     * @return error
     */
    Throwable getError();


}
