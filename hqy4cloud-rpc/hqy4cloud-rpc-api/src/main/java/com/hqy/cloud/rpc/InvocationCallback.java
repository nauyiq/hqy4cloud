package com.hqy.cloud.rpc;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 15:24
 */
public interface InvocationCallback {

    /**
     * rpc remote call back.
     * @param obj  request.
     */
    void doCallback(Object obj);

}
