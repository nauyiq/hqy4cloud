package com.hqy.cloud.id.component.snowflake.core;

import com.hqy.cloud.id.component.snowflake.exception.InitWorkerIdException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/23 13:45
 */
@Slf4j
public abstract class AbstractSnowflakeHolder implements SnowflakeHolder {

    protected static final int MAX_WORKER_ID = 1023;
    protected int workerId = -1;
    protected volatile boolean initOk = false;


    @Override
    public boolean initWorkerId(String serviceName) throws InitWorkerIdException {
        if (initOk) {
            return true;
        }
        try {
            doInit(serviceName);
            return true;
        } catch (Throwable cause) {
            log.warn("Failed execute to init workerId, cause: {}", cause.getMessage());
            return false;
        }
    }

    /**
     * do init service name
     * @param serviceName service
     */
    protected abstract void doInit(String serviceName);

    @Override
    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }
}
