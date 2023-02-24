package com.hqy.rpc.thrift.support;

import com.hqy.rpc.common.support.RPCContext;

/**
 * ThriftContext.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/19 16:15
 */
public class ThriftContext extends Context {

    private final RPCContext rpcContext;

    public ThriftContext() {
        this(null);
    }

    public ThriftContext(RPCContext rpcContext) {
        this.rpcContext = rpcContext;
    }

    public RPCContext getRpcContext() {
        return rpcContext;
    }


}
