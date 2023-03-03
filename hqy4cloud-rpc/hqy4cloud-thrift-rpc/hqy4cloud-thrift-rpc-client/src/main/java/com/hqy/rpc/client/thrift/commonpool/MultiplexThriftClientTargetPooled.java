package com.hqy.rpc.client.thrift.commonpool;

import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.client.thrift.support.ThriftClientManagerWrapper;
import com.hqy.rpc.common.RPCServerAddress;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.cloud.util.AssertUtil;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.hqy.rpc.common.CommonConstants.*;

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

    private GenericKeyedObjectPool<RPCServerAddress, T> pool;

    private final ThriftClientTargetBaseKeyedFactory<T> factory;

    private final GenericKeyedObjectPoolConfig<T> config;

    private final String serviceName;


    public MultiplexThriftClientTargetPooled(RPCModel rpcModel, Class<T> serviceType, ThriftClientManagerWrapper clientManagerWrapper) {
        AssertUtil.notNull(rpcModel, "RPCContext should not be null.");
        AssertUtil.notNull(serviceType, "Rpc interface class type should not be null.");
        AssertUtil.notNull(clientManagerWrapper, "ThriftClientManagerWrapper should not be null.");
        this.serviceName = rpcModel.getName();
        this.factory = new ThriftClientTargetBaseKeyedFactory<>(serviceName, serviceType, clientManagerWrapper);
        this.config = initializeObjectPoolConfig(rpcModel);
        this.pool = new GenericKeyedObjectPool<>(factory, config);

    }



    public void refreshObjectPooled(List<Invoker<T>> invokers) {
        close();
        pool = new GenericKeyedObjectPool<>(factory, config);
        initializeObjectPooled(invokers);
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
            if (pool != null && pool.isClosed()) {
                pool.clear();
                pool.close();
                pool = null;
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
