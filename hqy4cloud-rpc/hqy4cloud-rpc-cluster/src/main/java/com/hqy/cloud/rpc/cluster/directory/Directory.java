package com.hqy.cloud.rpc.cluster.directory;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.registry.common.context.CloseableService;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.rpc.Invocation;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.cluster.router.RouterChain;
import com.hqy.cloud.rpc.model.RpcModel;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Directory.
 * Directory represents a collection composed of multiple Invokers, and a series of operations such as routing processing,
 * load balancing, and cluster fault tolerance are implemented on the basis of Directory
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/30 15:41
 */
public interface Directory<T> extends CloseableService {

    /**
     * get service interface type.
     * @return service interface type.
     */
    Class<T> getInterface();

    /**
     * get provider rpc service name
     * @return provider service name.
     */
    String getProviderServiceName();

    /**
     * Based on the incoming Invocation request, Filter the self-maintained Invoker collection and return the eligible Invoker collection
     * @param invocation    invoker condition
     * @return              invokers
     * @throws RpcException non-catch
     */
    List<Invoker<T>> list(Invocation invocation) throws RpcException;

    /**
     * get list invokers, include all invokers from registry
     * @return invokers
     */
    List<Invoker<T>> getAllInvokers();

    /**
     * get rpc consumer model
     * @return metadata
     */
    RpcModel getRPCModel();

    /**
     * get application model
     * @return application model
     */
    ApplicationModel getModel();

    /**
     * get remote rpc provider application model
     * @return remote rpc provider application model
     */
    ApplicationModel getProviderModel();


    /**
     * invalidate an invoker, add it into reconnect task, remove from list next time
     * will be recovered by address refresh notification or reconnect success notification
     *
     * @param invoker invoker to invalidate
     */
    void addInvalidateInvoker(Invoker<T> invoker);

    /**
     * disable an invoker, remove from list next time
     * will be removed when invoker is removed by address refresh notification
     * using in service offline notification
     * @param invoker invoker to invalidate
     */
    void addDisabledInvoker(Invoker<T> invoker);

    /**
     * isDestroyed
     * @return isDestroyed
     */
    boolean isDestroyed();

    /**
     * interfaces server for registry have instances.
     * @return isEmpty
     */
    default boolean isEmpty() {
        return CollectionUtils.isEmpty(getAllInvokers());
    }

    /**
     * get router chain
     * @return RouterChain
     */
    RouterChain<T> getRouterChain();





}
