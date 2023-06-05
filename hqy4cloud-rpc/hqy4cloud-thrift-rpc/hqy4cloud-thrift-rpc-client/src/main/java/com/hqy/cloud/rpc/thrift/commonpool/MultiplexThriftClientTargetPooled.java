package com.hqy.cloud.rpc.thrift.commonpool;

import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.model.RPCServerAddress;
import com.hqy.cloud.rpc.threadpool.ExecutorRepository;
import com.hqy.cloud.rpc.thrift.support.ThriftClientManagerWrapper;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.pool2.impl.DefaultPooledObjectInfo;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.hqy.cloud.rpc.CommonConstants.*;

/**
 * MultiplexThriftClientTargetPooled.
 * IO multiplexing + object pooling.
 * @see ThriftClientTargetBaseKeyedFactory
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/12 15:57
 */
public class MultiplexThriftClientTargetPooled<T> {

    private static final Logger log = LoggerFactory.getLogger(MultiplexThriftClientTargetPooled.class);

    private final String serviceName;
    private volatile GenericKeyedObjectPool<RPCServerAddress, T> pool;
    private final ThriftClientTargetBaseKeyedFactory<T> factory;
    private final GenericKeyedObjectPoolConfig<T> config;
    private final int delayDisconnectScheduled;
    private final ScheduledExecutorService scheduledExecutorService;
    private final AtomicBoolean refreshed = new AtomicBoolean(false);
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public MultiplexThriftClientTargetPooled(RPCModel rpcModel, Class<T> serviceType, ThriftClientManagerWrapper clientManagerWrapper, ExecutorRepository executorRepository) {
        AssertUtil.notNull(rpcModel, "RPCContext should not be null.");
        AssertUtil.notNull(serviceType, "Rpc interface class type should not be null.");
        AssertUtil.notNull(clientManagerWrapper, "ThriftClientManagerWrapper should not be null.");
        this.serviceName = rpcModel.getName();
        this.factory = new ThriftClientTargetBaseKeyedFactory<>(serviceName, serviceType, clientManagerWrapper);
        this.scheduledExecutorService = executorRepository.getCommonScheduledExecutorService();
        this.delayDisconnectScheduled = rpcModel.getParameter(RPC_CLIENT_DELAY_DISCONNECT_TIME, 5);
        this.config = initializeObjectPoolConfig(rpcModel);
        this.pool = new GenericKeyedObjectPool<>(factory, config);
    }

    /**
     * refresh this object pool.
     * @param invokers refresh to pool invokes.
     */
    public void refreshObjectPooled(List<Invoker<T>> invokers) {
        if (closed.compareAndSet(true, false)) {
            // pool already close. rebuild pool and initialize pool.
            pool = new GenericKeyedObjectPool<>(factory, config);
            initializeObjectPooled(invokers);
            return;
        }

        //all objectInfo.
        Map<String, List<DefaultPooledObjectInfo>> allObjects = pool.listAllObjects();
        Set<String> keySet = allObjects.keySet();
        if (MapUtils.isEmpty(allObjects)) {
            initializeObjectPooled(invokers);
        } else {
            List<Invoker<T>> shouldInitializeInvokers = new LinkedList<>();
            for (Invoker<T> invoker : invokers) {
                String key = invoker.getModel().getServerAddress().toString();
                if (!keySet.contains(key)) {
                    shouldInitializeInvokers.add(invoker);
                    keySet.add(key);
                }
            }
            initializeObjectPooled(shouldInitializeInvokers);
            // disconnect client.
            keySet.removeAll(invokers.stream().map(invoker -> invoker.getModel().getServerAddress().toString()).collect(Collectors.toSet()));
            disconnectObjectPoolThriftClient(allObjects, keySet);
        }
    }

    private void disconnectObjectPoolThriftClient(Map<String, List<DefaultPooledObjectInfo>> allObjects, Set<String> shouldDisconnectKeys) {
        if (CollectionUtils.isNotEmpty(shouldDisconnectKeys)) {
            if (log.isInfoEnabled()) {
                log.info("Do disconnect thrift client, disconnect connections: {}.", JsonUtil.toJson(shouldDisconnectKeys));
            }
            scheduledExecutorService.schedule(() -> {
                List<RPCServerAddress> rpcServerAddresses = shouldDisconnectKeys.stream().map(key -> JsonUtil.toBean(key, RPCServerAddress.class)).collect(Collectors.toList());
                rpcServerAddresses.forEach(address -> {
                    try {
                        invalidTargetClient(address, this.pool.borrowObject(address));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }, delayDisconnectScheduled, TimeUnit.SECONDS);
        }
    }

    public synchronized void initializeObjectPooled(List<Invoker<T>> invokers) {
        factory.refreshFramedClientConnectorMap(invokers);
        for (Invoker<T> invoker : invokers) {
            try {
                pool.addObject(invoker.getModel().getServerAddress());
            } catch (Exception e) {
                log.warn("Failed execute to add object. cause {}", e.getMessage());
            }
        }
    }


    private GenericKeyedObjectPoolConfig<T> initializeObjectPoolConfig(RPCModel rpcModel) {
        GenericKeyedObjectPoolConfig<T> pool = new GenericKeyedObjectPoolConfig<>();
        int processors = Runtime.getRuntime().availableProcessors();
        int minIdle = rpcModel.getParameter(POOL_MIN_IDLE_PER_KEY, 0);
        int maxIdle = rpcModel.getParameter(POOL_MAX_IDLE_PER_KEY, processors);
        int maxTotal = rpcModel.getParameter(POOL_MAX_TOTAL, processors * 2);
        pool.setMaxTotal(maxTotal);
        pool.setMinIdlePerKey(minIdle);
        pool.setMaxTotalPerKey(maxIdle);
        pool.setTestOnBorrow(true);
        pool.setTestOnReturn(false);
        //支持jmx管理扩展
        pool.setJmxEnabled(true);
        pool.setJmxNamePrefix("ThriftClientPool");
        return pool;
    }


    public String gerServiceInfo(T service) {
      return factory.gerServiceInfo(service);
    }

    public T getTargetClient(RPCServerAddress address) throws Exception {
        return pool.borrowObject(address);
    }

    public void returnTargetClient(RPCServerAddress address, T target) {
        pool.returnObject(address, target);
    }


    public void close() {
        try {
            if (pool != null && closed.compareAndSet(false, true)) {
                Map<String, List<DefaultPooledObjectInfo>> map = pool.listAllObjects();
                if (MapUtils.isNotEmpty(map)) {
                    // 连接不为空, 等待5秒后清除对象池并关闭连接池
                    scheduledExecutorService.schedule(() -> {
                        pool.clear();
                        pool.close();
                        pool = null;
                    }, delayDisconnectScheduled, TimeUnit.SECONDS);
                }
                factory.closeConnectionCache(serviceName);
            }
        } catch (Throwable cause) {
            log.warn(cause.getMessage(), cause);
        }
    }


    public void invalidTargetClient(RPCServerAddress serverAddress, T target) throws Exception {
        pool.invalidateObject(serverAddress, target);
    }
}
