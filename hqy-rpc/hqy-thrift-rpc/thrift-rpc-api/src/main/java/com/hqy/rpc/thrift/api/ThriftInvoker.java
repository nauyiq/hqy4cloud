package com.hqy.rpc.thrift.api;

import com.facebook.nifty.client.FramedClientConnector;
import com.hqy.rpc.api.Invocation;
import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.api.protocol.AbstractInvoker;
import com.hqy.rpc.common.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/6 17:27
 */
public class ThriftInvoker<T> extends AbstractInvoker<T> {

    private static final Logger log = LoggerFactory.getLogger(ThriftInvoker.class);



    public ThriftInvoker(Class<T> serviceType, Metadata metadata, ) {

    }

    @Override
    protected Object doInvoke(Invocation invocation) throws Throwable {
        return null;
    }
}
