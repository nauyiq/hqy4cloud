package com.hqy.rpc.registry.node;

import com.hqy.rpc.common.URL;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 16:30
 */
public interface NodeService {

    /**
     * get url.
     *
     * @return url.
     */
    URL getUrl();

    /**
     * is available.
     *
     * @return available.
     */
    boolean isAvailable();

    /**
     * destroy.
     */
    void destroy();


}
