package com.hqy.cloud.util.identity;

import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/22 14:13
 */
public class ProjectSnowflakeIdWorker {

    private ProjectSnowflakeIdWorker() {}

    private static ProjectSnowflakeIdWorker instance = null;

    private final SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    public static ProjectSnowflakeIdWorker getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (ProjectSnowflakeIdWorker.class) {
                if (Objects.isNull(instance)) {
                    instance = new ProjectSnowflakeIdWorker();
                }
            }
        }
        return instance;
    }

    public long nextId() {
        return snowflakeIdWorker.nextId();
    }

}
