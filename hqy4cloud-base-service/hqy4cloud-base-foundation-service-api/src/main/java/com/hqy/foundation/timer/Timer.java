package com.hqy.foundation.timer;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @link org.apache.dubbo.common.timer
 * Schedules {@link TimerTask}s for one-time future execution in a background thread.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 17:27
 */
public interface Timer {

    /**
     * Schedules the specified {@link TimerTask} for one-time execution after the specified delay.
     * @param task      Schedules task.
     * @param delay     delay processing.
     * @param timeUnit  time unit.
     * @return a handle which is associated with the specified task.
     */
    Timeout newTimeout(TimerTask task, long delay, TimeUnit timeUnit);

    /**
     * Releases all resources acquired by this {@link Timer} and cancels all
     * tasks which were scheduled but not executed yet.
     * @return the handles associated with the tasks which were canceled by this method
     */
    Set<Timeout> stop();

    /**
     * the timer is stop
     * @return true for stop
     */
    boolean isStop();


}
