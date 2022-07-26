package com.hqy.rpc.monitor.suport;

import cn.hutool.core.map.MapUtil;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.monitor.Monitor;
import com.hqy.rpc.monitor.MonitorFactory;
import com.hqy.util.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * AbstractMonitorFactory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/18 10:27
 */
public abstract class AbstractMonitorFactory implements MonitorFactory {
    private static final Logger log = LoggerFactory.getLogger(AbstractMonitorFactory.class);

    /**
     * The lock for getting monitor center
     */
    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     *  The monitor centers Map<MonitorServerAddress, Registry>
     */
    private static final Map<String, Monitor> MONITORS = MapUtil.newConcurrentHashMap(2);
//    private static final Map<String, Future<Monitor>> FUTURES = MapUtil.newConcurrentHashMap(2);
//    private static final ExecutorService MONITOR_EXECUTOR = new ThreadPoolExecutor(0, 10, 60L,
//            TimeUnit.SECONDS, new SynchronousQueue<>(), new NamedThreadFactory("RPC-MONITOR-CENTER", true));

    public static Collection<Monitor> getMonitor() {
        return Collections.unmodifiableCollection(MONITORS.values());
    }

    @Override
    public Monitor getMonitor(RPCModel rpcModel) {
        String key = rpcModel.toServiceString();
        Monitor monitor = MONITORS.get(key);
//        Future<Monitor> future = FUTURES.get(key);
        if (monitor != null) {
            return monitor;
        }

        LOCK.lock();
        try {
            monitor = MONITORS.get(key);
//            future = FUTURES.get(key);
            if (monitor != null) {
                return monitor;
            }

//            final RPCModel monitorRpcModel = rpcModel;
//            future = MONITOR_EXECUTOR.submit(() -> {
                try {
                    Monitor m = createMonitor(rpcModel);
                    MONITORS.put(key, m);
//                    FUTURES.remove(key);
                    return m;
                } catch (Throwable e) {
                    log.warn("Create monitor failed, monitor data will not be collected until you fix this problem. monitorRpcModel: " + rpcModel, e);
                    return null;
                }
//            });
//            FUTURES.put(key, future);
//            return future.get();
        } finally {
            LOCK.unlock();
        }

    }

    /**
     * create monitor.
     * @param monitorRpcModel {@link RPCModel}
     * @return                {@link Monitor}
     */
    protected abstract Monitor createMonitor(RPCModel monitorRpcModel);
}
