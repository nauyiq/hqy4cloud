package com.hqy.cloud.sharding.id;

import com.hqy.cloud.util.identity.SnowflakeIdWorker;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/30
 */
public abstract class AbstractSnowflakeIdGen implements SnowflakeIdGen {

    private final Object lock = new Object();
    protected volatile SnowflakeIdWorker idWorker;


    @Override
    public long nextId() {
        if (idWorker == null) {
            synchronized (lock) {
                if (idWorker == null) {
                    idWorker = new SnowflakeIdWorker(getWorkerId());
                }
            }
        }
        return idWorker.nextId();
    }

    /**
     * 获取雪花算法的workerId
     * @return worker id.
     */
    protected abstract long getWorkerId();



}
