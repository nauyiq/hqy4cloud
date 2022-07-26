package com.hqy.rpc.client.thrift;

import com.facebook.swift.service.ThriftService;
import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.rpc.client.thrift.support.ThriftClientManagerFactory;
import com.hqy.rpc.client.thrift.support.ThriftClientManagerWrapper;
import com.hqy.rpc.cluster.client.AbstractClient;
import com.hqy.rpc.cluster.directory.Directory;
import com.hqy.rpc.common.CommonConstants;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.monitor.Monitor;
import com.hqy.rpc.monitor.thrift.ThriftMonitorFactory;
import com.hqy.rpc.registry.api.RegistryFactory;
import com.hqy.rpc.thrift.service.ThriftContextClientHandleService;
import com.hqy.rpc.thrift.handler.client.support.CollectionClientEventHandler;
import com.hqy.rpc.thrift.handler.client.support.SeataGlobalTransactionClientEventHandler;
import com.hqy.rpc.thrift.proxy.JdkProxyFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.hqy.rpc.common.CommonConstants.RPC_CLIENT_WORKER_THREAD_COUNTS;

/**
 * Thrift rpc client for {@link AbstractClient}.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 14:34
 */
public abstract class ThriftRPCClient extends AbstractClient {
    private static final Logger log = LoggerFactory.getLogger(ThriftRPCClient.class);

    private volatile ThriftClientManagerWrapper clientManager;
    private final RegistryFactory registryFactory;

    public ThriftRPCClient(RegistryFactory registryFactory) {
        super(new JdkProxyFactory());
        this.registryFactory = registryFactory;
    }


    @Override
    protected <T> Directory<T> createDirectory(Class<T> serviceClass) {
        return createDirectory(serviceClass, checkAnnotation(serviceClass));
    }

    @Override
    protected <T> Directory<T> createDirectory(Class<T> serviceClass, String application) {
        RPCModel context = getConsumerRpcModel();
        if (clientManager == null) {
            synchronized (this.registryFactory) {
                if (clientManager == null) {
                    this.clientManager = initClientFactory();
                }
            }
        }
        return new ThriftDynamicDirectory<>(application, context, serviceClass, clientManager, registryFactory);
    }


    private ThriftClientManagerWrapper initClientFactory() {
        RPCModel rpcModel = getConsumerRpcModel();
        //client worker count.
        int workerCount = rpcModel.getParameter(RPC_CLIENT_WORKER_THREAD_COUNTS, Runtime.getRuntime().availableProcessors() * 2);
        //client event handler services
        List<ThriftContextClientHandleService> services = createClientHandlerServices(rpcModel);
        log.info("Start create ThriftClientManager, workerCount: {}, services:{}", workerCount, services.size());

        ThriftClientManagerFactory factory = new ThriftClientManagerFactory();
        return factory.createThriftClientManager(workerCount, services);
    }

    private List<ThriftContextClientHandleService> createClientHandlerServices(RPCModel rpcModel) {
        //seata transactional support.
        SeataGlobalTransactionClientEventHandler seataGlobalTransactionClientEventHandler = new SeataGlobalTransactionClientEventHandler();
        //collection support.
        CollectionClientEventHandler collectionClientEventHandler = createCollectionClientEventHandler(rpcModel);

        return Arrays.asList(seataGlobalTransactionClientEventHandler, collectionClientEventHandler);
    }

    private CollectionClientEventHandler createCollectionClientEventHandler(RPCModel rpcModel) {
        //common-collector default monitor server.
        String application = rpcModel.getParameter(CommonConstants.THRIFT_MONITOR_APPLICATION_NAME, MicroServiceConstants.COMMON_COLLECTOR);
        ThriftMonitorFactory thriftMonitorFactory = new ThriftMonitorFactory(this);
        Monitor monitor = thriftMonitorFactory.getMonitor(RPCModel.setApplication(application));
        return new CollectionClientEventHandler(monitor);
    }


    /**
     * 检查 @ThriftService 并且返回注解的value（节点服务名称）
     * @param service rpc接口
     * @return 节点服务名称
     */
    protected static String checkAnnotation(Class<?> service) {
        ThriftService thriftService = service.getAnnotation(ThriftService.class);
        if (Objects.isNull(thriftService)) {
            throw new RpcException("@@@ Only ThriftService supported, class:" + service.getSimpleName());
        }
        String value = thriftService.value();
        if (StringUtils.isBlank(value)) {
            throw new RpcException("@@@ @ThriftService Annotation value not specified, class:" + service.getSimpleName());
        }
        return value;
    }

    /**
     * get consumer side rpc context.
     * @param <T>          rpc service type.
     * @return            {@link RPCModel}.
     */
    protected abstract <T> RPCModel getConsumerRpcModel();
}
