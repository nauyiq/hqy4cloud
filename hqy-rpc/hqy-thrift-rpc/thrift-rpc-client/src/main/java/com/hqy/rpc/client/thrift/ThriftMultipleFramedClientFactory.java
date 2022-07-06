package com.hqy.rpc.client.thrift;

import com.facebook.nifty.client.FramedClientConnector;
import com.hqy.rpc.common.Metadata;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Composite Thrift client connector factory.
 * apache common pool and NIO link multiplexing
 * @author qiyuan.hong
 * @date 2022-07-06 22:43
 */
public class ThriftMultipleFramedClientFactory<T> extends BasePooledObjectFactory<T> {

    private static final Logger log = LoggerFactory.getLogger(ThriftMultipleFramedClientFactory.class);

    private final Metadata metadata;

    private final Class<T> serviceClass;

    private FramedClientConnector[] connectors;




    @Override
    public T create() throws Exception {
        return null;
    }

    @Override
    public PooledObject<T> wrap(T obj) {
        return null;
    }
}
