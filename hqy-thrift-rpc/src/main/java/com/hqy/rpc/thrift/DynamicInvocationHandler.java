package com.hqy.rpc.thrift;

import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.rpc.regist.UsingIpPort;
import com.hqy.rpc.route.AbstractRPCRouter;
import com.hqy.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 每个RPCService 接口对应一个Handler， 可以接受nacos的事件通知
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-13 9:56
 */
@Slf4j
public class DynamicInvocationHandler<T> extends AbstractRPCRouter
        implements InvocationHandler {

    private final InvokeCallback callback;
    private GenericObjectPoolConfig<T> config;
    private MultiplexThriftServiceFactory<T> factory;
    //灰度服务的对象池
    private ObjectPool<T> objectPoolGray;
    //白度服务的对象池
    private ObjectPool<T> objPoolWhite;
    //所有类型的对象池
    private ObjectPool<T> objPoolAll;
    //高优先级的对象池
    private ObjectPool<T> objPoolHighPriority;
    //ip
    private static final String hostIp = IpUtil.getHostAddress();

    public DynamicInvocationHandler(Class<T> service, List<UsingIpPort> addressesGray, List<UsingIpPort> addressesWhite, InvokeCallback callback) {
        config = new GenericObjectPoolConfig<>();
        int cpu = Runtime.getRuntime().availableProcessors();
        int minIdle = cpu * 6;
        config.setMinIdle(minIdle);
        config.setMaxIdle(minIdle * 8);
        config.setMaxIdle(minIdle * 8);
        this.factory = new MultiplexThriftServiceFactory<>(service, addressesGray, addressesWhite, hostIp);
        initializeObjPool(addressesGray,addressesWhite);
        this.callback = callback;
    }

    private void initializeObjPool(List<UsingIpPort> addressesGray, List<UsingIpPort> addressesWhite) {
        super.setGrayProviders(addressesGray);
        super.setWhiteProviders(addressesWhite);

        //初始化对象池
        initializeObjPoolOfMode(addressesGray, GrayWhitePub.GRAY);


    }

    private void initializeObjPoolOfMode(List<UsingIpPort> usingIpPorts, GrayWhitePub pub) {
        if (GrayWhitePub.GRAY.equals(pub)) {
//            setGrayProviders();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }

}
