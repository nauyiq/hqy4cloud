package com.hqy.cloud.lock.annotation;

import com.hqy.cloud.lock.common.LockConstants;

/**
 * 分布式锁注解
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
public @interface DistributeLock {

    /**
     * 锁的场景
     * @return 场景不能为空
     */
    String scene();

    /**
     * 加锁的key，优先取key()，如果没有，则取keyExpression()
     * @return 加锁的key
     */
    String key() default LockConstants.NONE_KEY;

    /**
     * SPEL表达式:
     * <pre>
     *     #id
     *     #insertResult.id
     * </pre>
     * @return key spel表达式
     */
    String keyExpression() default LockConstants.NONE_KEY;

    /**
     * 超时时间，毫秒
     * 默认情况下不设置超时时间，会自动续期
     * @return 超时时间
     */
    int expireTime() default LockConstants.DEFAULT_EXPIRE_TIME;

    /**
     * 加锁等待时长，毫秒
     * 默认情况下不设置等待时长，不做等待
     * @return 加锁等待时长
     */
    int waitTime() default LockConstants.DEFAULT_WAIT_TIME;



}
