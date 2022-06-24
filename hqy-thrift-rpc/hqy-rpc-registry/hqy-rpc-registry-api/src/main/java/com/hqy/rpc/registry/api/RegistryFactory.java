package com.hqy.rpc.registry.api;

import com.hqy.rpc.common.URL;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 16:39
 */
public interface RegistryFactory {

    /**
     * Connect to the registry
     * @param url Registry address, is not allowed to be empty
     * @return    Registry reference, never return empty value
     */
    Registry getRegistry(URL url);

}
