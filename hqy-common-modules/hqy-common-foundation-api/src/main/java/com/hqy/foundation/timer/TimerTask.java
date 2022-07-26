package com.hqy.foundation.timer;

import java.util.concurrent.TimeUnit;

/**
 * @link org.apache.dubbo.common.timer
 * A task which is executed after the delay specified with {@link Timer#newTimeout(TimerTask, long, TimeUnit)} (TimerTask, long, TimeUnit)}.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 17:25
 */
public interface TimerTask {

    /**
     * Executed after the delay specified with {@link Timer#newTimeout(TimerTask, long, TimeUnit)}.
     * @param timeout    timeout a handle which is associated with this task
     * @throws Exception throw exception
     */
    void run(Timeout timeout) throws Exception;

}
