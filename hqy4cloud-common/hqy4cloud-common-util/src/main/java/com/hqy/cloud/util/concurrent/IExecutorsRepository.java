package com.hqy.cloud.util.concurrent;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.thread.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 通用的线程池service仓库.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 10:52
 */
@Slf4j
public class IExecutorsRepository extends Thread {
    private static final AtomicBoolean DESTROYED = new AtomicBoolean(false);
    private static final Map<String, IExecutorService> I_EXECUTOR_SERVICE_MAP = MapUtil.newConcurrentHashMap();
    private static final Map<String, ScheduledExecutorService> SCHEDULED_EXECUTOR_SERVICE_MAP = MapUtil.newConcurrentHashMap();

    public IExecutorsRepository() {
        Runtime.getRuntime().addShutdownHook(this);
    }

    public static IExecutorService getExecutor(String name) {
        AssertUtil.notEmpty(name, "Executor name should not be empty.");
        return I_EXECUTOR_SERVICE_MAP.get(name);
    }

    public static void setExecutor(String name, IExecutorService executorService) {
        AssertUtil.notEmpty(name, "Executor name should not be empty.");
        AssertUtil.notNull(executorService, "Executor service should not be null.");
        I_EXECUTOR_SERVICE_MAP.put(name, executorService);
    }

    public static IExecutorService newExecutor(String name) {
        AssertUtil.notEmpty(name, "Executor name should not be empty.");
        if (I_EXECUTOR_SERVICE_MAP.containsKey(name)) {
            return I_EXECUTOR_SERVICE_MAP.get(name);
        }
        IExecutorService iExecutorService = new AbstractIExecutorService(name) {};
        I_EXECUTOR_SERVICE_MAP.put(name, iExecutorService);
        return iExecutorService;
    }

    public static IExecutorService newExecutor(String name, int coreSize, int maxSize) {
        AssertUtil.notEmpty(name, "Executor name should not be empty.");
        if (I_EXECUTOR_SERVICE_MAP.containsKey(name)) {
            return I_EXECUTOR_SERVICE_MAP.get(name);
        }
        IExecutorService iExecutorService = new AbstractIExecutorService(name, coreSize, maxSize) {};
        I_EXECUTOR_SERVICE_MAP.put(name, iExecutorService);
        return iExecutorService;
    }


    public static ScheduledExecutorService newScheduledExecutor(String name) {
        AssertUtil.notEmpty(name, "Executor name should not be empty.");
        if (SCHEDULED_EXECUTOR_SERVICE_MAP.containsKey(name)) {
            return SCHEDULED_EXECUTOR_SERVICE_MAP.get(name);
        }
        ScheduledExecutorService service = Executors.newScheduledThreadPool(IExecutorService.DEFAULT_CORE_SIZE, new NamedThreadFactory(name));
        SCHEDULED_EXECUTOR_SERVICE_MAP.put(name, service);
        return service;
    }

    public static ScheduledExecutorService newSingleScheduledExecutor(String name) {
        AssertUtil.notEmpty(name, "Executor name should not be empty.");
        if (SCHEDULED_EXECUTOR_SERVICE_MAP.containsKey(name)) {
            return SCHEDULED_EXECUTOR_SERVICE_MAP.get(name);
        }
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(name));
        SCHEDULED_EXECUTOR_SERVICE_MAP.put(name, service);
        return service;
    }

    public static void shutDown(String name) {
        IExecutorService executor = getExecutor(name);
        if (executor != null && !executor.getExecutorService().isShutdown()) {
            executor.getExecutorService().shutdown();
        }
    }

    public static void shutDownAll() {
        I_EXECUTOR_SERVICE_MAP.keySet().forEach(IExecutorsRepository::shutDown);
        SCHEDULED_EXECUTOR_SERVICE_MAP.values().forEach(ExecutorService::shutdown);
    }

    @Override
    public void run() {
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("Destroyed executor repository now.");
        }
        if (DESTROYED.compareAndSet(false, true)) {
            shutDownAll();
        }
    }
}
