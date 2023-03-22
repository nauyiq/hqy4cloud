package com.hqy.cloud.rpc;

/**
 * CloseableService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/5 11:29
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
