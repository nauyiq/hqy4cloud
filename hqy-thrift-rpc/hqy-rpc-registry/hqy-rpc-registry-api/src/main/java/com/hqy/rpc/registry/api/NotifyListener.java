package com.hqy.rpc.registry.api;

import com.hqy.rpc.common.URL;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 13:45
 */
public interface NotifyListener {

    /**
     * Triggered when a service change notification is received.
     * @param urls The list of registered information
     */
    void notify(List<URL> urls);


}
