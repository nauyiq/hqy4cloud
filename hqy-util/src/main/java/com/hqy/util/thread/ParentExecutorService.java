package com.hqy.util.thread;

import java.util.Objects;

/**
 * 全局线程池 继承ExecutorServiceProject
 * @author qy
 * @create 2021/7/22 23:11
 */
public class ParentExecutorService extends ExecutorServiceProject {

    private ParentExecutorService(int coreSize, int maxSize, int capacity, String poolThreadName) {
        super(coreSize, maxSize, capacity, poolThreadName);
    }

    private static volatile ParentExecutorService instance = null;

    public ParentExecutorService getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (ParentExecutorService.class) {
                if (Objects.isNull(instance)) {
                    instance = new ParentExecutorService(3, Runtime.getRuntime().availableProcessors() * 4, 512, "ParentExecutor");
                }
            }
        }
        return instance;
    }


}
