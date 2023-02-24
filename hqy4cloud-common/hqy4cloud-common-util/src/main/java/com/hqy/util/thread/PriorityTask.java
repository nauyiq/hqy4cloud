package com.hqy.util.thread;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 支持优先级的任务（线程任务）
 * @author qy
 * @create 2021/7/22 22:55
 */
@Data
@AllArgsConstructor
public class PriorityTask implements Comparable<PriorityTask>, Runnable {

    private int priority;

    private Runnable runnable;

    public static PriorityTask newInstance(int priority, Runnable runnable) {
        return new PriorityTask(priority, runnable);
    }

    public static PriorityTask newInstance(Runnable runnable) {
        return new PriorityTask(ExecutorServiceProject.PRIORITY_IMMEDIATE, runnable);
    }

    @Override
    public int compareTo(PriorityTask current) {
        if (current.priority > this.priority) {
            return 1;
        } else if (current.priority == priority) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public void run() {
        this.runnable.run();
    }
}
