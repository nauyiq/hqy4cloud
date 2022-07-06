package com.hqy.rpc.cluster.router;

import com.hqy.rpc.common.Metadata;

/**
 * RouterFactory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/30 17:34
 */
public interface RouterFactory {

    /**
     * create router.
     * @param metadata Metadata
     * @return         Router
     */
    Router createRouter(Metadata metadata);

}
