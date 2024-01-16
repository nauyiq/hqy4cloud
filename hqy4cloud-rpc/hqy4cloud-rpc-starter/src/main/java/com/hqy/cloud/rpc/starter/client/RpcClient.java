package com.hqy.cloud.rpc.starter.client;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.registry.common.context.BeanRepository;
import com.hqy.cloud.rpc.service.RPCService;
import lombok.extern.slf4j.Slf4j;

/**
 * RpcClient.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/8
 */
@Slf4j
public class RpcClient {
    public static volatile Client client = null;

    public static void checkClient() {
        if (client == null) {
            client = BeanRepository.getInstance().getBean(Client.class);
        }

        if (client == null || !client.isAvailable()) {
            throw new RpcException(RpcException.REGISTRY_EXCEPTION, "Rpc client is available.");
        }
    }

    public static <T extends RPCService> T getRemoteService(Class<T> serviceClass) {
        checkClient();
        return client.getRemoteService(serviceClass);
    }

    public static <T extends RPCService> T getRemoteService(Class<T> serviceClass, String hashFactor) {
        checkClient();
        return client.getRemoteService(serviceClass, hashFactor);
    }






}
