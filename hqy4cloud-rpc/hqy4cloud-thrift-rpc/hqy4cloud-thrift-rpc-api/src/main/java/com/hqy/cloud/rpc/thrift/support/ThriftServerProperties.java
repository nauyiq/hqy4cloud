package com.hqy.cloud.rpc.thrift.support;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/7 17:30
 */
@ConfigurationProperties(prefix = "hqy4cloud.thrift")
public class ThriftServerProperties {

    /**
     * connect rpc service port.
     */
    private int rpcPort;

    /**
     * thrift service connect failure for retry time.
     */
    private int connectRetryTime = 64;

    /**
     * netty boss group thread number.
     */
    private int boosThreads = 1;

    /**
     * thread min idle.
     */
    private int threadMinIdle = 4;

    /**
     * netty io worker thread number.
     */
    private int workerThreads = Runtime.getRuntime().availableProcessors() + 1;

    public int getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(int rpcPort) {
        this.rpcPort = rpcPort;
    }

    public int getConnectRetryTime() {
        return connectRetryTime;
    }

    public void setConnectRetryTime(int connectRetryTime) {
        this.connectRetryTime = connectRetryTime;
    }

    public int getBoosThreads() {
        return boosThreads;
    }

    public void setBoosThreads(int boosThreads) {
        this.boosThreads = boosThreads;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }

    public int getThreadMinIdle() {
        return threadMinIdle;
    }

    public void setThreadMinIdle(int threadMinIdle) {
        this.threadMinIdle = threadMinIdle;
    }
}
