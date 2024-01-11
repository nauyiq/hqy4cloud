package com.hqy.cloud.rpc.starter.client;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.registry.common.context.CloseableService;
import com.hqy.cloud.rpc.InvocationCallback;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.RpcStarter;
import com.hqy.cloud.rpc.cluster.ClusterMode;
import com.hqy.cloud.rpc.threadpool.ExecutorRepository;

import java.util.Map;

/**
 * rpc consumer visual angle.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13
 */
public interface Client extends RpcStarter {

    /**
     * init rpc client.
     */
    void initialize();

    /**
     * Get remote invoker.
     * @param serviceClass rpc interface.
     * @param application  application name.
     * @param <T>          rpc interface type
     * @return             {@link Invoker}
     */
    <T> Invoker<T> getRemoteInvoker(Class<T> serviceClass, String application);

    /**
     * Get remote services: All services exposed to the RPC interface can be invoked as if they were local methods
     * @param serviceClass  rpc provider service class.
     * @return              rpc provider service proxy.
     * @throws RpcException rpc exception.
     */
    <T> T getRemoteService(Class<T> serviceClass) throws RpcException;


    /**
     * Get remote services: All services exposed to the RPC interface can be invoked as if they were local methods
     * @param serviceClass        rpc provider service class.
     * @param invocationCallback  callback
     * @return                    rpc provider service proxy.
     * @throws RpcException       rpc exception.
     */
    <T> T getRemoteService(Class<T> serviceClass, InvocationCallback invocationCallback) throws RpcException;


    /**
     * Get application services : All services exposed to the RPC interface can be invoked as if they were local methods
     * @param serviceClass rpc provider service class.
     * @param application  application.name
     * @return             rpc provider service proxy.
     * @throws RpcException rpc exception.
     */
    <T> T getApplicationService(Class<T> serviceClass, String application) throws RpcException;

    /**
     * Get remote services: All services exposed to the RPC interface can be invoked as if they were local methods
     * @param serviceClass  rpc provider service class.
     * @param hashFactor    use cluster.
     * @return              rpc provider service proxy.
     * @throws RpcException rpc exception.
     */
    <T> T getRemoteService(Class<T> serviceClass, String hashFactor) throws RpcException;


    /**
     * Get remote services: All services exposed to the RPC interface can be invoked as if they were local methods
     * @param serviceClass  rpc provider service class.
     * @param hashFactor    use cluster.
     * @param revision      choose service revision
     * @return              rpc provider service proxy.
     * @throws RpcException rpc exception.
     */
    <T> T getRemoteService(Class<T> serviceClass, String hashFactor, String revision) throws RpcException;


    /**
     * Get remote master service: Not support any loadBalance strategy, Just get master service from registry.
     * @param serviceClass  rpc provider service class.
     * @return              rpc provider service proxy.
     * @throws RpcException rpc exception.
     */
    <T> T getRemoteMasterService(Class<T> serviceClass) throws RpcException;

    /**
     * Get remote master service: Not support any loadBalance strategy, Just get master service from registry.
     * @param serviceClass       rpc provider service class.
     * @param invocationCallback callback.
     * @param clusterMode        choose cluster mode.
     * @return                   rpc provider service proxy.
     * @throws RpcException      rpc exception.
     */
    <T> T getRemoteMasterService(Class<T> serviceClass, InvocationCallback invocationCallback, ClusterMode clusterMode) throws RpcException;

    /**
     * Get remote master service: Not support any loadBalance strategy, Just get master service from registry.
     * @param serviceClass       rpc provider service class.
     * @param attachments        attachments
     * @param clusterMode        choose cluster mode.
     * @param invocationCallback callback
     * @return                   rpc provider service proxy.
     * @throws RpcException      rpc exception. {@link RpcException#getCode()}
     */
    <T> T getRemoteService(Class<T> serviceClass, Map<String, Object> attachments, ClusterMode clusterMode, InvocationCallback invocationCallback) throws RpcException;








}
