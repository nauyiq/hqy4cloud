package com.hqy.cloud.util.concurrent.async.tool;

import com.hqy.cloud.util.concurrent.AbstractIExecutorService;
import com.hqy.cloud.util.concurrent.IExecutorService;
import com.hqy.cloud.util.concurrent.IExecutorsRepository;
import com.hqy.cloud.util.concurrent.async.tool.callback.IGroupCallback;
import com.hqy.cloud.util.concurrent.async.tool.wrapper.WorkerWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 类入口，可以根据自己情况调整core线程的数量
 * copy - JD asyncTool
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 14:29
 */
@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
public class Async {

    private static final String NAME = "Async-tool";
    private static final int CORE_SIZE = 2;

    static {
        IExecutorsRepository.setExecutor(NAME, new AbstractIExecutorService(NAME) {});
    }


    public static boolean beginWork(long timeout, List<WorkerWrapper> wrappers) throws ExecutionException, InterruptedException {
        IExecutorService executor = IExecutorsRepository.getExecutor(NAME);
        if (executor == null) {
            return false;
        }
        return beginWork(timeout, executor.getExecutorService(), wrappers);
    }

    public static boolean beginWork(long timeout, ExecutorService executorService, WorkerWrapper... workerWrapper) throws ExecutionException, InterruptedException {
        if(workerWrapper == null || workerWrapper.length == 0) {
            return false;
        }
        List<WorkerWrapper> workerWrappers =  Arrays.stream(workerWrapper).collect(Collectors.toList());
        return beginWork(timeout, executorService, workerWrappers);
    }

    public static boolean beginWork(long timeout, ExecutorService executorService, List<WorkerWrapper> wrappers) throws ExecutionException, InterruptedException {
        if (CollectionUtils.isEmpty(wrappers)) {
            return false;
        }
        //定义一个map，存放所有的wrapper，key为wrapper的唯一id，value是该wrapper，可以从value中获取wrapper的result
        Map<String, WorkerWrapper> forParamUseWrappers = new ConcurrentHashMap<>(wrappers.size());
        CompletableFuture[] futures = new CompletableFuture[wrappers.size()];
        for (int i = 0; i < wrappers.size(); i++) {
            WorkerWrapper wrapper = wrappers.get(i);
            futures[i] = CompletableFuture.runAsync(() -> wrapper.work(executorService, timeout, forParamUseWrappers), executorService);
        }
        try {
            CompletableFuture.allOf(futures).get(timeout, TimeUnit.MILLISECONDS);
            return true;
        } catch (TimeoutException e) {
            Set<WorkerWrapper> set = new HashSet<>();
            totalWorkers(wrappers, set);
            for (WorkerWrapper wrapper : set) {
                wrapper.stopNow();
            }
            return false;
        }
    }

    public static void beginWorkAsync(long timeout, ExecutorService executorService, IGroupCallback groupCallback, WorkerWrapper... workerWrapper) {
        if (groupCallback == null) {
            groupCallback = new IGroupCallback() {};
        }
        IGroupCallback finalGroupCallback = groupCallback;
        if (executorService != null) {
            executorService.submit(() -> {
                try {
                    boolean success = beginWork(timeout, executorService, workerWrapper);
                    if (success) {
                        finalGroupCallback.success(Arrays.asList(workerWrapper));
                    } else {
                        finalGroupCallback.failure(Arrays.asList(workerWrapper), new TimeoutException());
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    finalGroupCallback.failure(Arrays.asList(workerWrapper), e);
                }
            });
        } else {
            IExecutorService executor = IExecutorsRepository.getExecutor(NAME);
            executor.execute(() -> {
                try {
                    boolean success = beginWork(timeout, executor.getExecutorService(), workerWrapper);
                    if (success) {
                        finalGroupCallback.success(Arrays.asList(workerWrapper));
                    } else {
                        finalGroupCallback.failure(Arrays.asList(workerWrapper), new TimeoutException());
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    finalGroupCallback.failure(Arrays.asList(workerWrapper), e);
                }
            });
        }

    }


    private static void totalWorkers(List<WorkerWrapper> workerWrappers, Set<WorkerWrapper> set) {
        set.addAll(workerWrappers);
        for (WorkerWrapper wrapper : workerWrappers) {
            if (wrapper.getNextWrappers() == null) {
                continue;
            }
            List<WorkerWrapper> wrappers = wrapper.getNextWrappers();
            totalWorkers(wrappers, set);
        }
    }








}
