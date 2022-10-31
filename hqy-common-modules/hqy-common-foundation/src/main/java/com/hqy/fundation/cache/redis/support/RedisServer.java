package com.hqy.fundation.cache.redis.support;

import com.hqy.fundation.cache.exception.RedisException;

import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @date 2022-04-04 14:43
 */
public enum RedisServer {

    /**
     * this RedisServer instance.
     */
    INSTANCE;

    public static RedisServer getInstance() {
        return INSTANCE;
    }


    /**
     * 获取redis分布式锁 基于while循环的自旋锁
     * @param lockName 锁名称
     * @param lockTime 锁多久 （毫秒）
     * @param timeOut  超时时间 （毫秒）
     * @return 是否获得锁
     */
    public boolean getLock(String lockName, long lockTime, long timeOut) {
        long beginTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - beginTime < timeOut) {
            Boolean result = SmartRedisManager.getInstance().setNx(lockName, "lock ok.", lockTime, TimeUnit.MILLISECONDS);
            if (result) {
                return true;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(30);
            } catch (InterruptedException e) {
                Thread.interrupted();
            } catch (Exception e) {
                throw new RedisException("@@@ RedisServer getLock lockName = " + lockName + "Exception", e);
            }
        }
        return false;
    }

    /**
     * 释放分布式锁
     * @param lockName 锁名称
     * @return 是否释放
     */
    public boolean releaseLock(String lockName) {
        try {
            return SmartRedisManager.getInstance().del(lockName);
        } catch (Exception e) {
            throw new RedisException("@@@ releaseLock getLock lockName = " + lockName + "Exception", e);
        }
    }




}
