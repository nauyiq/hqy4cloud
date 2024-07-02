package com.hqy.cloud.util.timer;


/**
 * @link org.apache.dubbo.common.timer
 * A handle associated with a {@link TimerTask} that is returned by a {@link Timer}.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 17:26
 */
public interface Timeout {

    /**
     * Returns the {@link Timer} that created this handle.
     * @return {@link Timer}
     */
    Timer timer();

    /**
     * Returns the {@link TimerTask} which is associated with this handle.
     * @return {@link TimerTask}
     */
    TimerTask task();

    /**
     * Returns {@code true} if and only if the {@link TimerTask} associated with this handle has been expired.
     * @return true for expired.
     */
    boolean isExpired();


    /**
     * Returns {@code true} if and only if the {@link TimerTask} associated
     * with this handle has been cancelled.
     * @return true for cancelled.
     */
    boolean isCancelled();


    /**
     * Attempts to cancel the {@link TimerTask} associated with this handle.
     * If the task has been executed or cancelled already, it will return with
     * no side effect.
     * @return True if the cancellation completed successfully, otherwise false
     */
    boolean cancel();


}
