package com.hqy.cloud.rpc.fallback;

import com.hqy.cloud.rpc.Invocation;
import com.hqy.cloud.rpc.Invoker;

/**
 * Fallback handler for rpc services.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/5 17:03
 */
public interface Fallback {

    /**
     * get fallback exception type
     * @return exceptionType
     */
    Class<? extends Exception> exceptionType();

    /**
     * Handle the block exception and provide fallback result.
     * @param invoker    rpc invoker
     * @param invocation rpc invocation
     * @param ex         ex
     * @return           rpc result
     * @throws Exception ex
     */
    Object handle(Invoker<?> invoker, Invocation invocation, Exception ex) throws Exception;

}
