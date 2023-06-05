package com.hqy.cloud.rpc.cluster.client;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.InvocationCallback;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.ProxyFactory;
import com.hqy.cloud.rpc.cluster.Cluster;
import com.hqy.cloud.rpc.cluster.ClusterContext;
import com.hqy.cloud.rpc.cluster.ClusterMode;
import com.hqy.cloud.rpc.cluster.directory.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hqy.cloud.common.base.lang.exception.RpcException.REGISTRY_EXCEPTION;


/**
 * AbstractClient.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 15:20
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractClient implements Client {

    private static final Logger log = LoggerFactory.getLogger(AbstractClient.class);

    private final ProxyFactory proxyFactory;

    /**
     * key   ->  interfaceClass name
     * value ->  Directory<interfaceClass type>
     */
    private static final Map<String, Directory> REGISTRY_DIRECTORY = MapUtil.newConcurrentHashMap();

    public AbstractClient(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    @Override
    public <T> T getRemoteService(Class<T> serviceClass) {
        return getRemoteService(serviceClass, StringConstants.DEFAULT);
    }

    @Override
    public <T> T getRemoteService(Class<T> serviceClass, String hashFactor) {
        return getRemoteService(serviceClass, hashFactor, null);
    }

    @Override
    public <T> T getRemoteService(Class<T> serviceClass, String hashFactor, InvocationCallback invocationCallback) throws RpcException {
        return getRemoteService(serviceClass, hashFactor, invocationCallback, null);
    }

    @Override
    public <T> T getRemoteService(Class<T> serviceClass, String hashFactor, InvocationCallback invocationCallback, ClusterMode clusterMode) throws RpcException {
        //interface name
        String serviceClassName = serviceClass.getName();
        Directory<T> directory = REGISTRY_DIRECTORY.computeIfAbsent(serviceClassName, name -> createDirectory(serviceClass));
        if (directory == null || directory.isDestroyed()) {
            throw new RpcException(REGISTRY_EXCEPTION, "Not available registry directory for serviceClass " + serviceClassName);
        }
        //cluster.
        Cluster cluster = ClusterContext.getCluster(directory.getProviderServiceName(), clusterMode);
        Invoker<T> invoker = cluster.join(directory);
        return proxyFactory.getProxy(invoker, invocationCallback);
    }

    @Override
    public <T> T getApplicationService(Class<T> serviceClass, String application) throws RpcException {
        Invoker<T> remoteInvoker = getRemoteInvoker(serviceClass, application);
        return proxyFactory.getProxy(remoteInvoker, null);
    }

    @Override
    public <T> Invoker<T> getRemoteInvoker(Class<T> serviceClass, String application) {
        //interface name
        String serviceClassName = serviceClass.getName();
        Directory<T> directory = REGISTRY_DIRECTORY.computeIfAbsent(serviceClassName, name -> createDirectory(serviceClass, application));
        if (directory == null || directory.isDestroyed()) {
            throw new RpcException(REGISTRY_EXCEPTION, "Not available registry directory for serviceClass " + serviceClassName);
        }
        //cluster.
        Cluster cluster = ClusterContext.getCluster(directory.getProviderServiceName());
        return cluster.join(directory);
    }

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }



    /**
     * create application directory.
     * @param serviceClass rpc service class.
     * @param <T>          rpc service type
     * @return             {@link Directory}
     */
    protected abstract <T> Directory<T> createDirectory(Class<T> serviceClass);

    /**
     * create application directory.
     * @param serviceClass rpc service class.
     * @param application  application name.
     * @param <T>          rpc service type
     * @return             {@link Directory}
     */
    protected abstract <T> Directory createDirectory(Class<T> serviceClass, String application);




}
