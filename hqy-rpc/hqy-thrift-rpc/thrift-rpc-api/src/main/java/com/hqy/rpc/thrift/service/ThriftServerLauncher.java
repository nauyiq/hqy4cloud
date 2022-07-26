package com.hqy.rpc.thrift.service;

import com.facebook.swift.service.ThriftEventHandler;
import com.hqy.rpc.api.service.RPCService;
import com.hqy.rpc.common.CommonConstants;

import java.util.Collections;
import java.util.List;

/**
 * rpc provider must implement this Launcher.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/14 9:51
 */
public interface ThriftServerLauncher {

    /**
     * get rpc provider server hashFactor. default hashFactor {@link com.hqy.rpc.common.CommonConstants#DEFAULT_HASH_FACTOR}
     * @return provider hashFactor.
     */
    default String getHashFactor() {
        return CommonConstants.DEFAULT_HASH_FACTOR;
    }

    /**
     * get rpc provider server wight.
     * @return  server wight.
     */
    default int getWight() {
        return CommonConstants.DEFAULT_WEIGHT;
    }

    /**
     * Get rpc provider services.
     * @return {@link RPCService}
     */
    List<RPCService> getRpcServices();

    /**
     * Get thrift server event handlers.
     * @return {@link ThriftEventHandler}
     */
    default List<ThriftServerContextHandleService> getThriftServerEventHandlerServices() {
        return Collections.emptyList();
    }
}
