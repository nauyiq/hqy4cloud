package com.hqy.rpc.thrift;

import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.rpc.nacos.NodeActivityObserver;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.fundation.common.base.project.UsingIpPort;
import com.hqy.rpc.route.AbstractRpcRouter;
import com.hqy.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 每个RPCService 接口对应一个Handler， 可以接受nacos的事件通知
 * @author qy
 * @date 2021-08-13 9:56
 */
@Slf4j
public class DynamicInvocationHandler<T> extends AbstractRpcRouter
        implements InvocationHandler, NodeActivityObserver {


    /**
     * rpc回调
     */
    private InvokeCallback callback;

    /**
     * 对象池配置类
     */
    private final GenericObjectPoolConfig<T> poolConfig;

    /**
     * 多路复用的ThriftService工厂
     */
    private final MultiplexThriftServiceFactory<T> factory;

    /**
     * 灰度服务的对象池
     */
    private ObjectPool<T> objectPoolGray;

    /**
     * 白度服务的对象池
     */
    private ObjectPool<T> objPoolWhite;

    /**
     * 所有类型的对象池
     */
    private ObjectPool<T> objPoolAll;

    /**
     * 高优先级的对象池
     */
    private ObjectPool<T> objPoolHighPriority;

    /**
     * ip
     */
    private static final String IP = IpUtil.getHostAddress();

    /**
     * 构造方法 添加对象连接池配置
     * @param service RPCService
     * @param addressesGray 灰度ip节点信息
     * @param addressesWhite 白度ip节点信息
     * @param callback rpc回调service
     */
    public DynamicInvocationHandler(Class<T> service, List<UsingIpPort> addressesGray, List<UsingIpPort> addressesWhite, InvokeCallback callback) {
        poolConfig = new GenericObjectPoolConfig<>();
        int cpu = Runtime.getRuntime().availableProcessors();
        int minIdle = cpu * 6;
        // 连接池中最少空闲的连接数,默认为0.
        poolConfig.setMinIdle(minIdle);
        // 连接池中最大空闲的连接数,默认为8.
        poolConfig.setMaxIdle(minIdle * 8);
        // 连接池最大连接数
        poolConfig.setMaxTotal(minIdle * 16);
        this.factory = new MultiplexThriftServiceFactory<>(service, addressesGray, addressesWhite, IP);
        //初始化对象池
        initializeObjPool(addressesGray,addressesWhite);
        this.callback = callback;
    }

    private void initializeObjPool(List<UsingIpPort> addressesGray, List<UsingIpPort> addressesWhite) {
        super.setGrayProviders(addressesGray);
        super.setWhiteProviders(addressesWhite);
        //初始化灰度对象池
        initializeObjPoolOfMode(addressesGray, GrayWhitePub.GRAY);
        //初始化白度对象池
        initializeObjPoolOfMode(addressesWhite, GrayWhitePub.WHITE);
        //初始化所有类型对象池
        addressesGray.addAll(addressesWhite);
        List<UsingIpPort> all = addressesGray.stream().distinct().collect(Collectors.toList());
        initializeObjPoolOfMode(all, null);
        //根据ip初始化高优先级的对象池
        initializeObjPoolHighPriority(all);
    }

    /**
     * 根据ip初始化高优先级的对象池
     * @param all 所有可用的节点信息
     */
    private void initializeObjPoolHighPriority(List<UsingIpPort> all) {
        if (CommonSwitcher.ENABLE_RPC_SAME_IP_HIGH_PRIORITY.isOff()) {
            log.info("@@@ CommonSwitcher.ENABLE_RPC_SAME_IP_HIGH_PRIORITY.isOff()");
            return;
        }


    }

    /**
     * 根据灰度白度模式 初始化连接池。
     * @param usingIpPorts 可用连接地址列表
     * @param pub null 表示不区分灰度百度
     */
    private void initializeObjPoolOfMode(List<UsingIpPort> usingIpPorts, GrayWhitePub pub) {

        if (Objects.isNull(pub)) {
            //加载所有的类型对象池
            try {
                //关闭连接池
                closePool(objPoolAll);
                //构建对象池
                generateObjectPool(usingIpPorts, null);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            if (log.isDebugEnabled()) {
                log.debug("@@@ initializeObjPool All. numIdle = {}", objPoolAll.getNumIdle());
            }
        } else if (GrayWhitePub.GRAY.equals(pub)) {
            //加载灰度类型对象池
            try {
                super.setGrayProviders(usingIpPorts);
                closePool(objectPoolGray);
                //构建对象池
                generateObjectPool(usingIpPorts, pub);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            if (log.isDebugEnabled()) {
                log.debug("@@@ initializeObjPool Gray. numIdle = {}", objectPoolGray.getNumIdle());
            }
        } else if (GrayWhitePub.WHITE.equals(pub)) {
            //加载白度类型对象池
            try {
                super.setWhiteProviders(usingIpPorts);
                closePool(objPoolWhite);
                //构建对象池
                generateObjectPool(usingIpPorts, pub);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            if (log.isDebugEnabled()) {
                log.debug("@@@ initializeObjPool White. numIdle = {}", objPoolWhite.getNumIdle());
            }
        } else {
            throw new IllegalStateException("@@@ Internal error, gray mode, invalid color value.");
        }

    }

    /**
     * 构建对象池
     * @param usingIpPorts 可用节点列表
     * @param pub 灰度白度
     * @throws Exception
     */
    private void generateObjectPool(List<UsingIpPort> usingIpPorts, GrayWhitePub pub) throws Exception {
        GenericObjectPool<T> objectPool = new GenericObjectPool<>(factory, poolConfig);
        objectPool.setMinIdle(usingIpPorts.size());
        objectPool.setTestOnBorrow(true);
        objectPool.setLifo(false);
        if (Objects.isNull(pub)) {
            objPoolAll = objectPool;
            objPoolAll.addObjects(usingIpPorts.size());
        } else if (GrayWhitePub.GRAY.equals(pub)) {
            objectPoolGray = objectPool;
            objectPoolGray.addObjects(usingIpPorts.size());
        } else if (GrayWhitePub.WHITE.equals(pub)) {
            objPoolWhite = objectPool;
            objPoolWhite.addObjects(usingIpPorts.size());
        }
    }

    /**
     * 关闭连接池
     * @param objectPool 连接池
     * @throws Exception
     */
    private void closePool(ObjectPool<T> objectPool) throws Exception {
        if (Objects.nonNull(objectPool)) {
            objPoolAll.clear();
            objPoolAll.close();
            objPoolAll = null;
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }

    @Override
    public void onAction(List<ClusterNode> grayNodes, List<ClusterNode> whiteNodes) {

    }
}
