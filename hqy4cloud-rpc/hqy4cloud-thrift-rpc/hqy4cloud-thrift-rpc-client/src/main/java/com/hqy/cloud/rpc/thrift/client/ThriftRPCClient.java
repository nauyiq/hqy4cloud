package com.hqy.cloud.rpc.thrift.client;

import cn.hutool.core.map.MapUtil;
import com.facebook.swift.service.ThriftService;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.common.context.BeanRepository;
import com.hqy.cloud.rpc.CommonConstants;
import com.hqy.cloud.rpc.cluster.directory.Directory;
import com.hqy.cloud.rpc.model.RpcModel;
import com.hqy.cloud.rpc.monitor.Monitor;
import com.hqy.cloud.rpc.monitor.thrift.ThriftMonitorFactory;
import com.hqy.cloud.rpc.starter.client.AbstractClient;
import com.hqy.cloud.rpc.threadpool.ExecutorRepository;
import com.hqy.cloud.rpc.thrift.client.proxy.JdkProxyFactory;
import com.hqy.cloud.rpc.thrift.service.ThriftContextClientHandleService;
import com.hqy.cloud.thrift.handler.support.CollectionClientEventHandler;
import com.hqy.cloud.util.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hqy.cloud.rpc.CommonConstants.RPC_CLIENT_WORKER_THREAD_COUNTS;

/**
 * Thrift rpc client for {@link AbstractClient}.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13
 */
public class ThriftRPCClient extends AbstractClient {
    private static final Logger log = LoggerFactory.getLogger(ThriftRPCClient.class);

    private volatile ThriftClientManagerWrapper clientManager;
    private volatile ExecutorRepository executorRepository;

    private final RpcModel rpcModel;
    private final AtomicBoolean destroyed = new AtomicBoolean(false);
    private final Registry registry;
    private final Map<Class<?>, Directory<?>> directoryMap = MapUtil.newConcurrentHashMap(8);

    public ThriftRPCClient(Registry registry, RpcModel rpcModel) {
        // TODO choose proxy factory.
        super(new JdkProxyFactory());
        this.registry = registry;
        this.rpcModel = rpcModel;
    }

    @Override
    protected <T> Directory<T> createDirectory(Class<T> serviceClass) {
        return createDirectory(serviceClass, checkAnnotation(serviceClass));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> Directory<T> createDirectory(Class<T> serviceClass, String application) {
        if (rpcModel == null || clientManager == null) {
            throw new UnsupportedOperationException("The rpc client not init.");
        }
        return (Directory<T>) directoryMap.computeIfAbsent(serviceClass,
                value -> new ThriftDynamicDirectory<>(application, rpcModel, serviceClass, clientManager, registry, executorRepository));
    }

    @Override
    public void start() throws RpcException {
        log.info("Start Thrift rpc client.");
    }

    @Override
    public void initialize() {
        // init thrift client manager
        initThriftClientManager();
        this.executorRepository = BeanRepository.getInstance().getBean(ExecutorRepository.class);
        AssertUtil.notNull(this.executorRepository, "Not found executorRepository from bean repository.");
    }

    @Override
    public boolean isAvailable() {
        return !destroyed.get();
    }

    @Override
    public void destroy() {
        if (destroyed.compareAndSet(false, true)) {
            // close thrift client manager.
            this.clientManager.close();
            // close directory
            if (MapUtil.isNotEmpty(this.directoryMap)) {
                directoryMap.values().forEach(Directory::destroy);
                directoryMap.clear();
            }
        }
    }

    private void initThriftClientManager() {
        //client worker count.
        int workerCount = this.rpcModel.getParameter(RPC_CLIENT_WORKER_THREAD_COUNTS, Runtime.getRuntime().availableProcessors() * 2);
        //client event handler services
        List<ThriftContextClientHandleService> services = createClientHandlerServices(rpcModel);
        log.info("Start create ThriftClientManager, workerCount: {}, services:{}", workerCount, services.size());
        this.clientManager = ThriftClientManagerFactory.createThriftClientManager(workerCount, services);
    }

    private List<ThriftContextClientHandleService> createClientHandlerServices(RpcModel rpcModel) {
        //collection support.
        CollectionClientEventHandler collectionClientEventHandler = createCollectionClientEventHandler(rpcModel);



        return Collections.singletonList(collectionClientEventHandler);
    }

    private CollectionClientEventHandler createCollectionClientEventHandler(RpcModel rpcModel) {
        //common-collector default monitor server.
        String application = rpcModel.getParameter(CommonConstants.THRIFT_MONITOR_APPLICATION_NAME, MicroServiceConstants.COMMON_COLLECTOR);
        ThriftMonitorFactory thriftMonitorFactory = new ThriftMonitorFactory(this);
        Monitor monitor = thriftMonitorFactory.getMonitor(RpcModel.copyOf(application, rpcModel));
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
            throw new RpcException("Only ThriftService supported, class:" + service.getSimpleName());
        }
        String value = thriftService.value();
        if (StringUtils.isBlank(value)) {
            throw new RpcException("@ThriftService Annotation value not specified, class:" + service.getSimpleName());
        }
        return value;
    }

    @Override
    public RpcModel getModel() {
        return this.rpcModel;
    }


}
