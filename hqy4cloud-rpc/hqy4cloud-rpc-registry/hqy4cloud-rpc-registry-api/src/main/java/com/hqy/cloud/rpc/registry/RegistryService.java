package com.hqy.cloud.rpc.registry;

import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.registry.api.NotifyListener;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 11:06
 */
public interface RegistryService {

    /**
     * Register data, such as : provider service, consumer address, route rule, override rule and other data.
     * @param rpcModel Registration information
     */
    void register(RPCModel rpcModel);


    /**
     * Unregister
     * @param rpcModel Registration information
     */
    void unregister(RPCModel rpcModel);


    /**
     * Subscribe to eligible registered data and automatically push when the registered data is changed.
     * @param rpcModel  Subscription condition
     * @param listener  A listener of the change event
     */
    void subscribe(RPCModel rpcModel, NotifyListener listener);


    /**
     * Unsubscribe
     * @param rpcModel Subscription condition
     * @param listener A listener of the change event
     */
    void unsubscribe(RPCModel rpcModel, NotifyListener listener);


    /**
     * Query the registered data that matches the conditions. Corresponding to the push mode of the subscription, this is the pull mode and returns only one result.
     * @param rpcModel Query condition
     * @return The registered information list, which may be empty, the meaning is the same as the parameters of {@link NotifyListener#notify(List)}.
     * @see NotifyListener#notify(List)
     */
    List<RPCModel> lookup(RPCModel rpcModel);


}
