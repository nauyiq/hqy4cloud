package com.hqy.cloud.rpc.threadpool;

import com.hqy.cloud.registry.common.context.CloseableService;
import com.hqy.cloud.util.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * FrameworkExecutorRepository.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/19 14:29
 */
public class FrameworkExecutorRepository implements CloseableService {

    private static final Logger log = LoggerFactory.getLogger(FrameworkExecutorRepository.class);

    private volatile boolean destroyed = false;

    private final ExecutorService sharedExecutor;

    private final ScheduledExecutorService sharedScheduledExecutor;

    private static final FrameworkExecutorRepository INSTANCE = new FrameworkExecutorRepository();

    private FrameworkExecutorRepository() {
        //sharedExecutor
        sharedExecutor = Executors.newCachedThreadPool(new NamedThreadFactory("roc-framework-shared-handler", true));
        // sharedScheduledExecutor
        sharedScheduledExecutor = Executors.newScheduledThreadPool(8, new NamedThreadFactory("rpc-shared-scheduler", true));
    }

    public static FrameworkExecutorRepository getInstance() { return INSTANCE; }

    /**
     * Get the shared schedule executor
     * @return ScheduledExecutorService
     */
    public ScheduledExecutorService getSharedScheduledExecutor() {
        return sharedScheduledExecutor;
    }

    /**
     * Get the default shared thread pool.
     *
     * @return ExecutorService
     */
    public ExecutorService getSharedExecutor() {
        return sharedExecutor;
    }

    @Override
    public boolean isAvailable() {
        return !destroyed;
    }

    @Override
    public synchronized void destroy() {
        log.info("Destroying Rpc executor repository.");
        this.destroyed = true;

        // sharedScheduledExecutor
        shutdownExecutorService(sharedScheduledExecutor, "scheduledExecutor");

        // shutdown share executor
        shutdownExecutorService(sharedExecutor, "sharedExecutor");

    }

    private void shutdownExecutorService(ExecutorService executorService, String name) {
        try {
            executorService.shutdownNow();
        } catch (Exception e) {
            String msg = "shutdown executor service [" + name + "] failed: ";
            log.warn(msg + e.getMessage(), e);
        }
    }
}
