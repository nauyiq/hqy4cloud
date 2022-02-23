package com.hqy.rpc.thrift;

import com.facebook.swift.service.RuntimeTApplicationException;
import com.hqy.fundation.common.base.lang.exception.NoAvailableProviderException;
import com.hqy.fundation.common.base.lang.exception.RpcException;
import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.rpc.nacos.NamingServiceClient;
import com.hqy.rpc.nacos.NodeActivityObserver;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.fundation.common.base.project.UsingIpPort;
import com.hqy.rpc.route.AbstractRpcRouter;
import com.hqy.util.IpUtil;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import com.hqy.util.thread.ParentExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.transport.TTransportException;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 每个RPCService 接口对应一个Handler， 可以接受nacos的事件通知
 *
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
    private static final String LOCAL_IP = IpUtil.getHostAddress();

    private int pubValue = GrayWhitePub.NONE.value;

    /**
     * 构造方法 添加对象连接池配置
     *
     * @param service        RPCService
     * @param addressesGray  灰度ip节点信息
     * @param addressesWhite 白度ip节点信息
     * @param callback       rpc回调service
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
        this.factory = new MultiplexThriftServiceFactory<>(service, addressesGray, addressesWhite, LOCAL_IP);
        //初始化对象池
        initializeObjPool(addressesGray, addressesWhite);
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
     *
     * @param all 所有可用的节点信息
     */
    private void initializeObjPoolHighPriority(List<UsingIpPort> all) {
        if (CommonSwitcher.ENABLE_RPC_SAME_IP_HIGH_PRIORITY.isOff()) {
            log.info("@@@ CommonSwitcher.ENABLE_RPC_SAME_IP_HIGH_PRIORITY.isOff()");
            return;
        }

        try {
            //初始化同环卡回环节点列表
            List<UsingIpPort> highPriorityIpList = new ArrayList<>();
            for (UsingIpPort usingIpPort : all) {
                //同ip优先 同环卡回环
                if (usingIpPort.getIp().equals(LOCAL_IP)) {
                    highPriorityIpList.add(usingIpPort);
                }
            }
            //关闭连接池
            closePool(objPoolHighPriority);
            this.objPoolHighPriority = null;

            if (CollectionUtils.isEmpty(highPriorityIpList)) {
                //如果没找到同IP的，尝试加载同环境的服务节点优先;
                String env = SpringContextHolder.getProjectContextInfo().getEnv();
                if (StringUtils.isNotBlank(env)) {
                    log.error("@@@ ProjectContextInfo.env is null!");
                } else {
                    for (UsingIpPort usingIpPort : all) {
                        if (env.equals(usingIpPort.getEnv())) {
                            highPriorityIpList.add(usingIpPort);
                        }
                    }
                }
            }

            if (CollectionUtils.isEmpty(highPriorityIpList)) {
                log.info("@@@ HighPriorityIpList is empty, initialize objPoolHighPriority == null.");
                objPoolHighPriority = null;
            } else {
                //构建对象池
                generateObjectPool(highPriorityIpList, GrayWhitePub.HIGH);
            }

            if (log.isDebugEnabled()) {
                log.debug("@@@ initializeObjPool objPoolHighPriority. numIdle = {}", objPoolHighPriority.getNumIdle());
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 根据灰度白度模式 初始化连接池。
     *
     * @param usingIpPorts 可用连接地址列表
     * @param pub          null 表示不区分灰度百度
     */
    private void initializeObjPoolOfMode(List<UsingIpPort> usingIpPorts, GrayWhitePub pub) {

        if (Objects.isNull(pub)) {
            //加载所有的类型对象池
            try {
                //关闭连接池
                closePool(objPoolAll);
                this.objPoolAll = null;

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
                this.objectPoolGray = null;
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
                this.objPoolWhite = null;
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
     *
     * @param usingIpPorts 可用节点列表
     * @param pub          灰度白度
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
        } else {
            objPoolHighPriority = objectPool;
            objPoolHighPriority.addObjects(usingIpPorts.size());
        }
    }

    /**
     * 关闭连接池
     *
     * @param objectPool 连接池
     * @throws Exception
     */
    private void closePool(ObjectPool<T> objectPool) throws Exception {
        if (Objects.nonNull(objectPool)) {
            objectPool.clear();
            objectPool.close();
        }

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object result = null;
        boolean rpcResult = false;
        //获取返回值类型 这里不能是数组 返回值只能是一个
        Type returnType = method.getGenericReturnType();
        //获取返回值的泛型参数
        if (CommonSwitcher.ENABLE_MSG_CHANNEL_4_GENERIC_RPC.isOn() && returnType instanceof ParameterizedType) {
            result = genericMsgRpcResult(returnType, method);
            return result;
        }
        //是否需要返回结果
        boolean needReturnTarget = true;
        //根据当前节点灰度值和有无开启灰度策略选择一个合适的对象池
        ObjectPool<T> objectPool = findSuitableObjPool();
        long fromTime = System.currentTimeMillis();
        T target = null;
        String targetInfo = null;
        try {
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.debug("@@@ ObjPool before borrowObject:  numActive={}, numIdle={}", objectPool.getNumActive(), objectPool.getNumIdle());
            }
            //从对象池中获取一个实例
            target = objectPool.borrowObject();
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.debug("@@@ ObjPool after borrowObject:  numActive={}, numIdle={}", objectPool.getNumActive(), objectPool.getNumIdle());
            }
            targetInfo = factory.gerServiceInfo(target);
            result = method.invoke(target, args);
            if (Objects.nonNull(callback)) {
                callback.onInvokeResult(result);
            }
            rpcResult = true;
        } catch (NoAvailableProviderException exception) {
            String msg = String.format("[DynamicInvocationHandler] method invoke failed for [%s], method:%s, args:%s",
                    "NoAvailableProviderException", method.getName(), Arrays.toString(args));
            log.error(msg, exception);
            needReturnTarget = false;
            //异步重试
            ParentExecutorService.getInstance().execute(() -> {
                try {
                    retryReadNodeInfo();
                } catch (Exception e) {
                    log.warn("retryReadNodeInfo exception, {}, {}", e.getClass().getName(), e.getMessage());
                }
            });
        } catch (ExecutionException e) {
            //并发异常，极有可能是执行了耗时的非半工的RPC方法
            String err = String.format("[DynamicInvocationHandler] method invoke failed, target:%s, method:%s, args:%s",
                    targetInfo, method.getName(), Arrays.toString(args));
            //TODO 异常采集.
            throw new RpcException(err + ", ExecutionException:" + e.getMessage(), e);
        } catch (Exception e) {
            boolean matchDisconnectedByServer = checkIfDisconnectedByServer(e);
            if (matchDisconnectedByServer) {
                log.warn("@@@ RPC-发现了严重的异常, 发起连接请求被服务端NIO通道的长连接拒绝了, 已尝试异步重连.");
                needReturnTarget = false;
            }
            String err = String.format("[DynamicInvocationHandler] method invoke failed, target:%s, method:%s, args:%s",
                    targetInfo, method.getName(), Arrays.toString(args));
            if (e instanceof InvocationTargetException && e.getCause() != null
                    && e.getCause() instanceof RuntimeTApplicationException && e.getCause().getCause() != null
                    && e.getCause().getCause() instanceof TApplicationException
                    && ((TApplicationException) e.getCause().getCause()).getType() == TApplicationException.MISSING_RESULT) {
                // 服务端返回null; 正常返回null 值。兼容thriftRPC 正常返回null值的场景
                log.warn("#### {} ; rpc service return null.", err);
            } else if (e instanceof TTransportException) {
                log.warn("#### invoke RPC method ,[Ignored Coll] Exception happen : {}, {} ", e.getClass().getName(), e.getMessage());
                needReturnTarget = false;
                throw new RpcException(err + ", exception:" + e.getClass().getName(), e);
            } else {
                log.warn("#### Invoke RPC method, exception happen : {}, {} ", e.getClass().getName(), e.getMessage());
                needReturnTarget = false;
                throw new RpcException(err + ", exception:" + e.getClass().getName(), e);
            }
        } finally {
            //记录一次rpc访问...
            //记录一次rpc访问...
            try {
                if (needReturnTarget) {
                    objectPool.returnObject(target);
                } else if (target != null) {
                    objectPool.invalidateObject(target);
                }
            } catch (Exception e) {
                String err = String.format("[DynamicInvocationHandler] return target failed, target:%s, method:%s, args:%s",
                        targetInfo, method.getName(), Arrays.toString(args));
                log.warn("WARN:{}, {}, {}", e.getClass().getName(), e.getMessage(), err);
            }

        }

        if(CommonSwitcher.JUST_4_TEST_DEBUG.isOn()){
            long duration = System.currentTimeMillis() - fromTime;
            log.debug("## invoke method:{}, args:{}, target:{}, costMills:{} , rpc_success={}",
                    method.getName(), Arrays.toString(args), targetInfo, duration, rpcResult);
        }

        return result;
    }

    private boolean checkIfDisconnectedByServer(Exception e) {
        return false;
    }

    /**
     * 发现有没有可用的节点服务 进行重试
     * 尽量使用异步调用.
     */
    private void retryReadNodeInfo() {
        //检查下灰度模式
        checkPubValue();
        if (!NamingServiceClient.getInstance().status()) {
            log.warn("@@@ NamingServiceClient status is close.");
            return;
        } else {

        }
    }

    private void checkPubValue() {
        ProjectContextInfo projectContextInfo = SpringContextHolder.getProjectContextInfo();
        Integer pubValue = projectContextInfo.getPubValue();
        if (CommonSwitcher.ENABLE_GRAY_MECHANISM.isOn()) {
            if (!pubValue.equals(GrayWhitePub.GRAY.value) && !pubValue.equals(GrayWhitePub.WHITE.value)) {
                log.warn("@@@ checkValue() 内部状态错误, 灰度机制不清晰!");
                this.pubValue = GrayWhitePub.NONE.value;
            } else {
                this.pubValue = pubValue;
            }
        } else {
            this.pubValue = GrayWhitePub.NONE.value;
        }

    }

    /**
     * 根据当前节点灰度值和有无开启灰度策略选择一个合适的对象池
     *
     * @return 合适的对象池
     */
    private ObjectPool<T> findSuitableObjPool() {
        ProjectContextInfo projectContextInfo = SpringContextHolder.getProjectContextInfo();
        if (CommonSwitcher.ENABLE_GRAY_MECHANISM.isOn()) {
            //启用灰度机制
            Integer pubValue = projectContextInfo.getPubValue();
            if (pubValue.equals(GrayWhitePub.GRAY.value)) {
                return objectPoolGray;
            } else if (pubValue.equals(GrayWhitePub.WHITE.value)) {
                return objPoolWhite;
            }
        } else {
            if (CommonSwitcher.ENABLE_RPC_SAME_IP_HIGH_PRIORITY.isOff()) {
                return objPoolAll;
            } else {
                //非灰度模式下，优先使用 同IP的服务节点
                if (Objects.isNull(objPoolHighPriority)) {
                    if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                        log.debug("@@@ 非灰度模式下，优先使用同IP的服务节点: ALL, {} ", LOCAL_IP);
                    }
                    return objPoolAll;
                } else {
                    if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                        log.debug("@@@ 非灰度模式下，优先使用同IP的服务节点: YES, {} ", LOCAL_IP);
                    }
                    return objPoolHighPriority;
                }
            }
        }
        throw new IllegalStateException("@@@ 内部状态错误，灰度机制不清晰! Internal error!");
    }


    private Object genericMsgRpcResult(Type returnType, Method method) {
        //是否支持泛型RPC-MSG通道（消息通道） 并且 返回值类型是不是参数化类型 默认关闭
        ParameterizedType parameterizedType = (ParameterizedType) returnType;
        String typeName = parameterizedType.getRawType().getTypeName();
        //如果是List Set Map Collection; thrift本身就支持
        boolean normal = typeName.equals(List.class.getTypeName())
                || typeName.equals(Collection.class.getTypeName())
                || typeName.equals(Set.class.getTypeName())
                || typeName.equals(Map.class.getTypeName());

        if (!normal) {
            log.info("@@@ DynamicHandler invoke returnType : {}", returnType);
        }

        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.debug("Generic return type  rpc: clazz = {}", method.getDeclaringClass().getName());
            log.debug("Generic return type  rpc: method = {}", method.getName());
        }
        //TODO 后续在补充
        return new Object();
    }

    @Override
    public void onAction(List<ClusterNode> grayNodes, List<ClusterNode> whiteNodes) {

    }
}
