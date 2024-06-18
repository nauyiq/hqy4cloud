package com.hqy.cloud.limiter.api;

import cn.hutool.core.date.SystemClock;

/**
 * 资源超限策略接口.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/6 16:01
 */
public interface Limiter {

    /**
     * 判断当前资源是否访问超限。
     * @param resource 资源
     * @return         是否超限
     */
    boolean isOverLimit(String resource);

    /**
     * System.currentTimeMillis() 这个API在JVM的实现层面会调用gettimeofday()这个方法，会存在如下几个问题
     * 调用gettimeofday()需要从用户态切换到内核态；
     * gettimeofday()的表现受Linux系统的计时器（时钟源）影响，在HPET计时器下性能尤其差；
     * 系统只有一个全局时钟源，高并发或频繁访问会造成严重的争用。
     * @return 返回系统时间
     */
     default long currentTimeMillis() {
        return SystemClock.now();
    }


}
