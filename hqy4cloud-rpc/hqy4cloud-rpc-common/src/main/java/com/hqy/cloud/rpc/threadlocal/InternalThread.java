package com.hqy.cloud.rpc.threadlocal;

/**
 * InternalThread.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/18 17:11
 */
public class InternalThread extends Thread {

    private InternalThreadLocalMap threadLocalMap;

    public InternalThread() {
    }

    public InternalThread(Runnable target) {
        super(target);
    }

    public InternalThread(ThreadGroup group, Runnable target) {
        super(group, target);
    }

    public InternalThread(String name) {
        super(name);
    }

    public InternalThread(ThreadGroup group, String name) {
        super(group, name);
    }

    public InternalThread(Runnable target, String name) {
        super(target, name);
    }

    public InternalThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    public InternalThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
    }

    /**
     * Returns the internal data structure that keeps the threadLocal variables bound to this thread.
     * Note that this method is for internal use only, and thus is subject to change at any time.
     */
    public final InternalThreadLocalMap threadLocalMap() {
        return threadLocalMap;
    }

    /**
     * Sets the internal data structure that keeps the threadLocal variables bound to this thread.
     * Note that this method is for internal use only, and thus is subject to change at any time.
     */
    public final void setThreadLocalMap(InternalThreadLocalMap threadLocalMap) {
        this.threadLocalMap = threadLocalMap;
    }
}
