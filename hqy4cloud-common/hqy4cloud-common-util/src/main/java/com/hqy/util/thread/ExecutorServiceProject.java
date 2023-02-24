package com.hqy.util.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * ExecutorServiceProject 实现业务级别线程池
 * 建议子类实现时 弄成单例使用
 * @author qy
 * @date 2021/7/22 22:06
 */
@Slf4j
public abstract class ExecutorServiceProject {

    /**
     * 线程池拒绝策略执行次数
     */
    private int rejectCounter = 0;

    /**
     * 核心线程个数
     */
    private int coreSize = 2;

    /**
     * 最大线程个数
     */
    private int maxSize = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 队列大小
     */
    private int capacity = 512;

    /**
     * 等待时间 2分钟
     */
    private static final long KEEP_ALIVE_TIME = 2L;

    /**
     * 阻塞队列
     */
    private BlockingDeque<Runnable> workQueue = new LinkedBlockingDeque<>(capacity);

    /**
     * 线程池名称
     */
    private String poolThreadName;

    /**
     * 线程工厂
     */
    private ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(poolThreadName + "-%d").build();

    public ExecutorServiceProject(String poolThreadName) {
        this.poolThreadName =  poolThreadName;
    }

    public ExecutorServiceProject(int coreSize, int maxSize, int capacity,String poolThreadName, ThreadFactory threadFactory) {
        this(coreSize, maxSize, capacity, poolThreadName);
        this.threadFactory = threadFactory;
    }

    public ExecutorServiceProject(int coreSize, int maxSize, int capacity,String poolThreadName, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        this(coreSize, maxSize, capacity, poolThreadName);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }

    public ExecutorServiceProject( int coreSize, int maxSize, int capacity, String poolThreadName) {
        super();
        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.capacity = capacity;
        this.workQueue = new LinkedBlockingDeque<>(capacity);
        this.poolThreadName = poolThreadName;
    }

    public void reset() {
        log.info("### reset work queue ...");
        int del = 0;
        if(workQueue.isEmpty()) {
            return;
        } else {
            for(Runnable xxRunnable : workQueue) {
                try {
                    if(xxRunnable instanceof PriorityTask && ((PriorityTask)xxRunnable).getRunnable()  instanceof CancelableRunnable) {
                        workQueue.remove(xxRunnable);
                        del++;
                    }
                    if(xxRunnable instanceof CancelableRunnable) {
                        workQueue.remove(xxRunnable);
                        del ++;
                    }
                } catch (Exception e) {
                    log.warn("error when try to remove CancelableRunnable job, ignore for next: {}", e.getMessage());
                }

            }
        }
        log.info("### deleted CancelableRunnable from workQueue, size={}", del);
    }

    /**
     * 拒绝策略
     */
    private RejectedExecutionHandler handler = (r, executor) -> {
        log.warn("WARN ~ ExecutorServiceProject rejectedExecution !");
        try {
            if (rejectCounter % 100 == 0) {
                //do something
                log.warn(poolThreadName + "rejectedExecution! \r\n rejectCounter = " + rejectCounter);
            }
        } catch (Exception e) {
            rejectCounter++;
        }

    };

    private final ThreadPoolExecutor EXECUTOR_SERVICE =
            new ThreadPoolExecutor(coreSize, maxSize, KEEP_ALIVE_TIME, TimeUnit.MINUTES, workQueue, threadFactory, handler);


    public int getWorkingQueueSize() {
        return workQueue.size();
    }


    public int getWorkingQueueCapacity() {
        return capacity;
    }


    /**
     * 队列长度是否超过百分一
     * @return
     */
    public boolean isQueueHundredthFull() {
        float using = (float) getWorkingQueueSize() / getWorkingQueueCapacity();
        return using > 0.01F;
    }

    /**
     * 线程池是否快满了？ (使用率超过75%)
     * @return
     */
    public boolean isQueueNearlyFull() {
        float using = (float) getWorkingQueueSize() / getWorkingQueueCapacity();
        return using > 0.75F;
    }

    /**
     * 队列是否占用超过一半？？？（使用率超过50%）
     * @return
     */
    public boolean isQueueHalfFull() {
        float using = (float)getWorkingQueueSize() / getWorkingQueueCapacity();
        return using > 0.50F;
    }

    /**
     * 立即马上，最高优先级
     */
    public static final int PRIORITY_IMMEDIATE = 0;

    /**
     * 较高优先级，高于默认优先级
     */
    public static final int PRIORITY_HIGH = 2;

    /**
     * 默认，中度优先级
     */
    public static final int PRIORITY_DEFAULT = 5;

    /**
     * 优先级低，低于默认优先级...
     */
    public static final int PRIORITY_LOW = 7;

    public ExecutorService getExecutorService() {
        return EXECUTOR_SERVICE;
    }

    public void execute(Runnable runnable) {
        execute(runnable, PRIORITY_DEFAULT);
    }

    public void execute(Runnable runnable, int priority) {
        execute(PriorityTask.newInstance(priority, runnable));
    }

    public void execute(PriorityTask priorityTask) {
        try {
            EXECUTOR_SERVICE.execute(priorityTask);
        }catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    public int getMaximumPoolSize() {
        return EXECUTOR_SERVICE.getMaximumPoolSize();
    }

}
