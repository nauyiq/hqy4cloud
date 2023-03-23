package com.hqy.cloud.id.component.snowflake.core;

import com.hqy.cloud.id.component.snowflake.exception.InitWorkerIdException;

/**
 * SnowflakeHolder.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/23 13:43
 */
public interface SnowflakeHolder {

    /**
     * init snowflake workerId.
     * @param serviceName service name
     * @return worker id.
     * @throws InitWorkerIdException init exception
     */
    boolean initWorkerId(String serviceName) throws InitWorkerIdException;

    /**
     * get worker id
     * @return worker id
     */
    int getWorkerId();


}
