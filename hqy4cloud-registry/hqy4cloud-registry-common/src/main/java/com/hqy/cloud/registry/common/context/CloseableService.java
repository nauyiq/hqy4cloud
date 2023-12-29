package com.hqy.cloud.registry.common.context;

/**
 * CloseableService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 13:49
 */
public interface CloseableService {

    /**
     * is available.
     * @return available.
     */
    boolean isAvailable();

    /**
     * destroy.
     */
    void destroy();

}
