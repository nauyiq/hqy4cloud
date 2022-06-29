package com.hqy.rpc;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 利用apache common pool ，对象缓存复用。+NIO链接多路复用。<br>
 * 通过暴露的updateAddresses 方法 来更新远程连接。<br>
 * <T> RPCService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 15:59
 */
public class MultiplexThriftServiceFactory<T> extends BasePooledObjectFactory<T> {

    private static final Logger log = LoggerFactory.getLogger(MultiplexThriftServiceFactory.class);

    @Override
    public T create() throws Exception {
        return null;
    }

    @Override
    public PooledObject<T> wrap(T obj) {
        return null;
    }
}
