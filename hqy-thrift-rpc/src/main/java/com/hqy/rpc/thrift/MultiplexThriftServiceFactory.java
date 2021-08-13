package com.hqy.rpc.thrift;

import com.facebook.nifty.client.FramedClientConnector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 利用apache common pool ，对象缓存复用。+NIO链接多路复用。<br>
 *  * 通过暴露的updateAddresses 方法 来更新远程连接。<br>
 *  <T> RPCService
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-13 14:43
 */
@Slf4j
public class MultiplexThriftServiceFactory<T> extends BasePooledObjectFactory<T> {

    private Class<T> serviceClass;

    private final AtomicInteger createCount = new AtomicInteger(0);

    private FramedClientConnector connectorsAll[];

    private FramedClientConnector connectorsGray[];

    private FramedClientConnector connectorsWhite[];

    private FramedClientConnector connectorsHighPriority[];

    /**
     * 可重入锁：多线程环境下（事件异步通知可用服务节点变化），保障 connectors 线程安全
     */
    private ReentrantLock lock = new ReentrantLock();

    @Override
    public T create() throws Exception {
        FramedClientConnector connector = null;


        return null;
    }

    @Override
    public PooledObject<T> wrap(T t) {
        return null;
    }

    private FramedClientConnector[] findSuitableConnectors() {


        return null;

    }
}
