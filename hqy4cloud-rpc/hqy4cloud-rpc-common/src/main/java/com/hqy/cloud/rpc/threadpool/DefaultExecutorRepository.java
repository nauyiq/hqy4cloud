package com.hqy.cloud.rpc.threadpool;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.util.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.hqy.cloud.rpc.CommonConstants.DEFAULT_COMMON_SCHEDULED_SERVICE_THREAD;
import static com.hqy.cloud.rpc.CommonConstants.RPC_COMMON_SCHEDULED_SERVICE_THREAD;

/**
 * DefaultExecutorRepository.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/1 14:08
 */
public class DefaultExecutorRepository implements ExecutorRepository {
    private final static Logger log = LoggerFactory.getLogger(DefaultExecutorRepository.class);

    private final RPCModel rpcModel;
    private final Object lock = new Object();
    private final Map<String, ExecutorService> repository = MapUtil.newConcurrentHashMap(4);
    private volatile ScheduledExecutorService commonScheduledExecutorService;

    public DefaultExecutorRepository(RPCModel rpcModel) {
        this.rpcModel = rpcModel;
    }

    @Override
    public ExecutorService createExecutorIfAbsent(String name, int count) {
        return repository.computeIfAbsent(name, value ->
                Executors.newFixedThreadPool(count, new NamedThreadFactory("RPC-" + name + "-service", true)));
    }

    @Override
    public ExecutorService getExecutor(String name) {
        return repository.get(name);
    }

    @Override
    public void updateThreadPool(String name, ExecutorService executor) {
        this.repository.put(name, executor);
    }

    @Override
    public ScheduledExecutorService getCommonScheduledExecutorService() {
        synchronized (lock) {
            if (this.commonScheduledExecutorService == null) {
                int coreSize = getCommonThreadNum();
                String name = rpcModel.getName();
                this.commonScheduledExecutorService = Executors.newScheduledThreadPool(coreSize,
                        new NamedThreadFactory("RPC-" + name + "-common-service", true));
            }
        }
        return this.commonScheduledExecutorService;
    }

    private int getCommonThreadNum() {
        return rpcModel.getParameter(RPC_COMMON_SCHEDULED_SERVICE_THREAD, DEFAULT_COMMON_SCHEDULED_SERVICE_THREAD);
    }

    @Override
    public void destroyAll() {
        if (commonScheduledExecutorService != null) {
            commonScheduledExecutorService.shutdown();
        }
        if (MapUtil.isNotEmpty(repository)) {
            for (ExecutorService service : repository.values()) {
                if (service != null && !service.isShutdown()) {
                    service.shutdown();
                }
            }
        }

    }

}
