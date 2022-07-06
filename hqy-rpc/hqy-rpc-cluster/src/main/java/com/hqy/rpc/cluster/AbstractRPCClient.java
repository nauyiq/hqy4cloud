package com.hqy.rpc.cluster;

import cn.hutool.core.map.MapUtil;
import com.facebook.swift.service.ThriftService;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.base.lang.exception.NoAvailableProviderException;
import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.rpc.api.*;
import com.hqy.rpc.cluster.directory.Directory;
import com.hqy.rpc.cluster.loadbalance.LoadBalance;
import com.hqy.rpc.registry.api.Registry;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/1 16:10
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractRPCClient implements Client {

    /**
     * key   ->  interfaceClass name
     * value ->  Directory
     */
    public static final Map<String, Directory> REGISTRY_DIRECTORY = MapUtil.newConcurrentHashMap();

    protected final Registry registry;

    protected LoadBalance loadBalance;

    protected final ProxyFactory proxyFactory;


    public AbstractRPCClient(Registry registry, LoadBalance loadBalance, ProxyFactory proxyFactory) {
        this.registry = registry;
        this.loadBalance = loadBalance;
        this.proxyFactory = proxyFactory;
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


    public <T> T getRemoteService(Class<T> serviceClass) {
        return getRemoteService(serviceClass, StringConstants.DEFAULT, null);
    }

    public <T> T getRemoteService(Class<T> serviceClass, String hashFactor) {
        return getRemoteService(serviceClass, hashFactor, null);
    }


    public <T> T getRemoteService(Class<T> serviceClass, String hashFactor, InvokerCallback invokerCallback) {
        String serviceModuleName = checkAnnotation(serviceClass);
        String serviceClassName = serviceClass.getName();

        Directory<T> directory = REGISTRY_DIRECTORY.computeIfAbsent(serviceClassName, s -> buildDirectory(serviceClass, serviceModuleName, registry));
        if (directory == null || directory.isDestroyed()) {
            throw new RpcException("Not available directory for serviceClass  " + serviceClassName);
        }

        Invocation invocation = RpcInvocation.createInvocation(invokerCallback, hashFactor);

        List<Invoker<T>> invokers = directory.list(invocation);
        if (CollectionUtils.isEmpty(invokers)) {
            //TODO do something
            throw new NoAvailableProviderException();
        }

        //load balance choose invoker.
        Invoker<T> invoker = loadBalance.select(invokers, directory.consumerMetadata(), invocation);

        return proxyFactory.getProxy(invoker);
    }

    protected abstract <T> Directory buildDirectory(Class<T> serviceClass, String serviceModuleName, Registry registry);
}
