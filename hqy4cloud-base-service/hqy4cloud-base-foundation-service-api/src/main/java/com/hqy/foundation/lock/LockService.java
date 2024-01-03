package com.hqy.foundation.lock;

import java.util.concurrent.TimeUnit;

/**
 * LockService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public interface LockService {

    /**
     * 是否上锁
     * @param lockName 锁名称
     * @return         是否上锁
     */
    boolean isLocked(String lockName);

    /**
     * 加锁
     * @param lockName 锁名称
     * @param lockTime 锁时间
     * @param timeUnit 时间单位
     */
    void lock(String lockName, long lockTime, TimeUnit timeUnit);

    /**
     * 尝试加锁，达到超时时间则不再尝试获取锁并返回false
     * @param lockName 锁名称
     * @param lockTime 锁时间
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     * @return         是否锁成功
     * @throws InterruptedException e.
     */
    boolean tryLock(String lockName, long lockTime, long timeout, TimeUnit timeUnit) throws InterruptedException;


    /**
     * 释放锁
     * @param lockName 锁名称
     */
    void unlock(String lockName);


}
