package com.hqy.cloud.lock.service.impl;

import com.hqy.cloud.lock.service.LockService;
import com.hqy.cloud.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redisson的分布式锁service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
@Slf4j
public record RedissonLockServiceImpl(RedissonClient redissonClient) implements LockService {

    @Override
    public boolean isLocked(String lockName) {
        if (StringUtils.isBlank(lockName)) {
            return false;
        }
        RLock lock = redissonClient.getLock(lockName);
        return lock.isLocked();
    }

    @Override
    public void lock(String lockName) {
        AssertUtil.notEmpty(lockName, "Lock key should not be empty.");
        RLock lock = redissonClient.getLock(lockName);
        lock.lock();
    }

    @Override
    public void lock(String lockName, long lockTime, TimeUnit timeUnit) {
        AssertUtil.notEmpty(lockName, "Lock key should not be empty.");
        RLock lock = redissonClient.getLock(lockName);
        lock.lock(lockTime, timeUnit);
    }

    @Override
    public boolean tryLock(String lockName, long timeout, TimeUnit timeUnit) throws InterruptedException {
        AssertUtil.notEmpty(lockName, "Lock key should not be empty.");
        RLock lock = redissonClient.getLock(lockName);
        return lock.tryLock(timeout, timeUnit);
    }

    @Override
    public boolean tryLock(String lockName, long lockTime, long timeout, TimeUnit timeUnit) throws InterruptedException {
        AssertUtil.notEmpty(lockName, "Lock key should not be empty.");
        RLock lock = redissonClient.getLock(lockName);
        return lock.tryLock(timeout, lockTime, timeUnit);
    }

    @Override
    public void unlock(String lockName) {
        RLock lock = redissonClient.getLock(lockName);
        lock.unlock();
    }
}
