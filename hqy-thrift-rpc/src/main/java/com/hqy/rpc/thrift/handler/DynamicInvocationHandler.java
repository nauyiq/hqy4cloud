package com.hqy.rpc.thrift.handler;

import com.facebook.swift.service.RuntimeTApplicationException;
import com.facebook.swift.service.RuntimeTTransportException;
import com.hqy.foundation.spring.event.ExceptionCollActionEvent;
import com.hqy.base.common.base.lang.exception.NoAvailableProviderException;
import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.base.common.base.project.UsingIpPort;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.rpc.nacos.NamingServiceClient;
import com.hqy.rpc.nacos.NodeActivityObserver;
import com.hqy.rpc.nacos.RegistryClient;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.rpc.route.AbstractRpcRouter;
import com.hqy.rpc.thrift.InvokeCallback;
import com.hqy.rpc.thrift.MultiplexThriftServiceFactory;
import com.hqy.rpc.thrift.RPCFlowController;
import com.hqy.rpc.thrift.ex.ThriftRpcHelper;
import com.hqy.rpc.transaction.TransactionContext;
import com.hqy.util.IpUtil;
import com.hqy.util.MathUtil;
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
 * 每个RPCService 接口对应一个Handler，
 * 同时也是nacos节点变化事件的观察者 可以观察到nacos节点变化事件
 * @author qy
 * @date 2021-08-13 9:56
 */
@Slf4j
public class DynamicInvocationHandler<T> extends AbstractRpcRouter
        implements InvocationHandler, NodeActivityObserver {

    /**
     * rpc回调
     */
    private final InvokeCallback callback;

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

    /**
     * nacos client。
     */
    private RegistryClient client;


    private int pubValue = GrayWhitePub.NONE.value;

    /**
     * 构造方法 添加对象连接池配置
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

    /**
     * 初始化对象池
     * @param addressesGray
     * @param addressesWhite
     */
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
        initializeObjPoolOfMode(all, GrayWhitePub.NONE);
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

        try {
            //初始化同环卡回环节点列表
            List<UsingIpPort> highPriorityIpList = new ArrayList<>();
            for (UsingIpPort usingIpPort : all) {
                //同ip优先 同环卡回环
                if (usingIpPort.getIp().equals(LOCAL_IP)) {
                    highPriorityIpList.add(usingIpPort);
                }
            }
            //关闭对象池
            closePool(objPoolHighPriority);
            if (this.objPoolHighPriority != null) {
                this.objPoolHighPriority = null;
            }

            //如果没找到同IP的，尝试加载同环境的服务节点优先;
            if (CollectionUtils.isEmpty(highPriorityIpList)) {
                String env = SpringContextHolder.getProjectContextInfo().getEnv();
                if (StringUtils.isBlank(env)) {
                    //上下文中的env为空 说明上下文没有被初始化 env默认使用dev
                    log.warn("@@@ ProjectContextInfo not registry.");
                    env = EnvironmentConfig.ENV_DEV;
                }
                for (UsingIpPort usingIpPort : all) {
                    if (env.equals(usingIpPort.getEnv())) {
                        highPriorityIpList.add(usingIpPort);
                    }
                }
            }

            if (CollectionUtils.isEmpty(highPriorityIpList)) {
                log.info("@@@ HighPriorityIpList is empty, initialize objPoolHighPriority == null.");
                objPoolHighPriority = null;
            } else {
                //构建对象池
                generateObjectPool(highPriorityIpList, GrayWhitePub.HIGH);
                if (log.isDebugEnabled()) {
                    log.debug("@@@ initializeObjPool objPoolHighPriority. numIdle = {}", objPoolHighPriority.getNumIdle());
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 根据灰度白度模式 初始化连接池。
     * @param usingIpPorts 可用连接地址列表
     * @param pub          null 表示不区分灰度百度
     */
    private void initializeObjPoolOfMode(List<UsingIpPort> usingIpPorts, GrayWhitePub pub) {

        if (GrayWhitePub.NONE.equals(pub)) {
            //加载所有的类型对象池
            try {
                //关闭连接池
                closePool(objPoolAll);
                this.objPoolAll = null;
                //构建对象池
                generateObjectPool(usingIpPorts, GrayWhitePub.NONE);
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
     * @param usingIpPorts 可用节点列表
     * @param pub          灰度白度
     * @throws Exception
     */
    private void generateObjectPool(List<UsingIpPort> usingIpPorts, GrayWhitePub pub) throws Exception {
        GenericObjectPool<T> objectPool = new GenericObjectPool<>(factory, poolConfig);
        objectPool.setMinIdle(usingIpPorts.size());
        objectPool.setTestOnBorrow(true);
        objectPool.setLifo(false);
        if (GrayWhitePub.NONE.equals(pub)) {
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
     * 清除内存并关闭对象连接池
     * @param objectPool 连接池
     */
    private void closePool(ObjectPool<T> objectPool) {
        try {
            if (Objects.nonNull(objectPool)) {
                objectPool.clear();
                objectPool.close();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        //反射执行返回结果
        Object result = null;
        //rpc执行结果
        boolean rpcResult = false;
        //获取返回值类型 这里不能是数组 返回值只能是一个
        Type returnType = method.getGenericReturnType();
        //获取返回值的泛型参数
        if (CommonSwitcher.ENABLE_MSG_CHANNEL_4_GENERIC_RPC.isOn() && returnType instanceof ParameterizedType) {
            result = genericMsgRpcResult(returnType, method);
            return result;
        }
        //如果是否是分布式事务方法，则在调用之前标记一下. 在thrift rpc调用过程中会进行事务传播.
        TransactionContext.makeThriftMethodTransactional(method);
        //是否需要返回结果
        boolean needReturnTarget = true;
        //根据当前节点灰度值和有无开启灰度策略选择一个合适的对象池
        ObjectPool<T> objectPool = findSuitableObjPool();
        //起始时间
        long fromTime = System.currentTimeMillis();
        //RpcService
        T target = null;
        //netty通道信息
        String targetInfo = "";

        try {
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.debug("@@@ ObjPool before borrowObject: numActive={}, numIdle={}", objectPool.getNumActive(), objectPool.getNumIdle());
            }
            //从对象池中获取一个实例
            target = objectPool.borrowObject();
            targetInfo = factory.gerServiceInfo(target);
            result = method.invoke(target, args);
            if (Objects.nonNull(callback)) {
                callback.onInvokeResult(result);
            }
            rpcResult = true;
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.debug("@@@ ObjPool after borrowObject: numActive={}, numIdle={}", objectPool.getNumActive(), objectPool.getNumIdle());
            }
        } catch (NoAvailableProviderException exception) {
            needReturnTarget = doNoAvailableProviderException(method, args, exception);
        } catch (ExecutionException e) {
            doExecutionException(method, args, targetInfo, e);
            needReturnTarget = false;
        } catch (Exception e) {
            needReturnTarget = doRpcException(method, args, targetInfo, e);
        } finally {
            try {
                if (needReturnTarget) {
                    objectPool.returnObject(target);
                } else if (target != null) {
                    objectPool.invalidateObject(target);
                }
                //记录一次rpc访问...
                RPCFlowController.getInstance().count(method.getName(), method.getDeclaringClass(), rpcResult);
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



    /**
     * rpc异常处理
     * @param method
     * @param args
     * @param targetInfo
     * @param e
     * @return
     */
    private boolean doRpcException(Method method, Object[] args, String targetInfo, Exception e) {
        boolean matchDisconnectedByServer = checkIfDisconnectedByServer(e);
        boolean needReturnTarget = true;
        if (matchDisconnectedByServer) {
            log.warn("@@@ RPC-发现了严重的异常, 发起连接请求被服务端NIO通道的长连接拒绝了, 已尝试异步重连.");
            needReturnTarget = false;
        }

        String err = String.format("[DynamicInvocationHandler] method invoke failed, target:%s, method:%s, args:%s",
                targetInfo, method.getName(), Arrays.toString(args));

        if (e instanceof InvocationTargetException && e.getCause() != null
                && e.getCause() instanceof RuntimeTApplicationException
                && e.getCause().getCause() != null
                && e.getCause().getCause() instanceof TApplicationException
                && ((TApplicationException) e.getCause().getCause()).getType() == TApplicationException.MISSING_RESULT) {
            // 服务端返回null; 正常返回null 值。兼容thriftRPC 正常返回null值的场景
            log.warn("#### {} ; rpc service return null.", err);
        } else if (e instanceof TTransportException) {
            log.warn("#### invoke RPC method ,[Ignored Coll] Exception happen : {}, {} ", e.getClass().getName(), e.getMessage());
            throw new RpcException(err + ", exception:" + e.getClass().getName(), e);
        } else {
            if (MathUtil.mathIf(10, 10000)) {
                ExceptionCollActionEvent event = new ExceptionCollActionEvent(DynamicInvocationHandler.class, e, 500);
                SpringContextHolder.publishEvent(event);
            }
            log.warn("#### Invoke RPC method, exception happen : {}, {} ", e.getClass().getName(), e.getMessage());
            throw new RpcException(err + ", exception:" + e.getClass().getName(), e);
        }
        return needReturnTarget;
    }

    /**
     * 并发异常处理
     * @param method 方法
     * @param args 方法参数
     * @param targetInfo rpc service netty channel 信息
     * @param e 异常
     */
    private void doExecutionException(Method method, Object[] args, String targetInfo, ExecutionException e) {
        //并发异常，极有可能是执行了耗时的非半工的RPC方法
        String err = String.format("[DynamicInvocationHandler] method invoke failed, target:%s, method:%s, args:%s",
                targetInfo, method.getName(), Arrays.toString(args));

        if (MathUtil.mathIf(5, 10000)) {
            //发送异常事件
            ExceptionCollActionEvent event = new ExceptionCollActionEvent(DynamicInvocationHandler.class, e, 2000, CommonResultCode.CONSUMING_TIME_RPC);
            SpringContextHolder.publishEvent(event);
        }

        throw new RpcException(err + ", ExecutionException:" + e.getMessage(), e);
    }

    /**
     * 发生NoAvailableProviderException 执行业务逻辑.
     * @param method
     * @param args
     * @param exception
     * @return
     */
    private boolean doNoAvailableProviderException(Method method, Object[] args, NoAvailableProviderException exception) {
        String msg = String.format("[DynamicInvocationHandler] method invoke failed for [%s], method:%s, args:%s",
                "NoAvailableProviderException", method.getName(), Arrays.toString(args));
        log.error(msg, exception);
        ParentExecutorService.getInstance().execute(() -> {
            try {
                //异步重试
                retryReadNodeInfo();
            } catch (Exception e) {
                log.warn("retryReadNodeInfo exception, {}, {}", e.getClass().getName(), e.getMessage());
            }
        });
        return false;
    }

    /**
     * 检查连接是否断开
     * @param e 异常
     * @return boolean
     */
    private boolean checkIfDisconnectedByServer(Exception e) {
        boolean matchDisconnectedByServer = false;
        if (e instanceof InvocationTargetException) {
            String keyword = "Client was disconnected by server";
            InvocationTargetException ite = (InvocationTargetException) e;

            if (Objects.nonNull(ite.getCause())
                    && ite.getCause().getClass().equals(RuntimeTTransportException.class)) {
                //thrift RuntimeTTransportException 异常
                RuntimeTTransportException iteCause = (RuntimeTTransportException) ite.getCause();
                log.warn("@@@ RuntimeTTransportException:{}", iteCause.getMessage());
                //客户端与服务端断开连接
                if (keyword.equals(iteCause.getMessage())) {
                    //继续重试一下
                    ParentExecutorService.getInstance().execute(this::retryReadNodeInfo, ParentExecutorService.PRIORITY_DEFAULT);
                    matchDisconnectedByServer = true;
                }
            }

        }
        return matchDisconnectedByServer;
    }

    /**
     * 发现有没有可用的节点服务 进行重试
     * 尽量使用异步调用.
     */
    private void retryReadNodeInfo() {
        //检查下灰度模式
        checkPubValue();
        if (Objects.isNull(client)) {
            log.warn("@@@ retryReadNodeInfo failure, registryClient is null for current handler.");
            return;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("@@@ retryReadNodeInfo start, color mode [{}]", pubValue);
            }
            List<ClusterNode> clusterNodes = NamingServiceClient.getInstance().loadProjectNodeInfo(client.getServiceNameEn(), true);
            //更新一下
            client.updateIpPorts(clusterNodes);
        }

        if (pubValue == GrayWhitePub.NONE.value) {
            //未启用灰度模式
            List<ClusterNode> allLivingNode = client.getAllLivingNode();
            log.info("@@@ retryReadNodeInfo, allNode, size:{}", allLivingNode.size());
            List<UsingIpPort> usingIpPorts = ThriftRpcHelper.convertToUip(allLivingNode);
            //刷新远程连接地址
            factory.updateAddress(usingIpPorts, usingIpPorts);
            //更新对象池
            initializeObjPoolOfMode(usingIpPorts, GrayWhitePub.NONE);
            //更新高优先级对象池
            initializeObjPoolHighPriority(usingIpPorts);
        } else if (pubValue == GrayWhitePub.WHITE.value) {
            //白度
            List<ClusterNode> whiteNodes = client.getAllLivingNode(GrayWhitePub.WHITE);
            log.info("@@@ retryReadNodeInfo, white node, size:{}", whiteNodes.size());
            List<UsingIpPort> usingIpPorts = ThriftRpcHelper.convertToUip(whiteNodes);
            //刷新远程连接地址
            factory.updateAddress(super.getGrayProviders() ,usingIpPorts);
            //更新对象池
            initializeObjPoolOfMode(usingIpPorts, GrayWhitePub.WHITE);
        } else {
            //灰度
            List<ClusterNode> grayNodes = client.getAllLivingNode(GrayWhitePub.GRAY);
            log.info("@@@ retryReadNodeInfo, gray node, size:{}", grayNodes.size());
            List<UsingIpPort> usingIpPorts = ThriftRpcHelper.convertToUip(grayNodes);
            //刷新远程连接地址
            factory.updateAddress(usingIpPorts, super.getWhiteProviders());
            //更新对象池
            initializeObjPoolOfMode(usingIpPorts, GrayWhitePub.GRAY);
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
        List<UsingIpPort> grayUipList = ThriftRpcHelper.convertToUip(grayNodes);
        List<UsingIpPort> whiteUipList = ThriftRpcHelper.convertToUip(whiteNodes);
        //刷新远程连接信息
        this.factory.updateAddress(grayUipList, whiteUipList);
        //更新对象池
        initializeObjPool(grayUipList, whiteUipList);
    }

    /**
     * 销毁对象池 释放内存
     */
    public void destroy() {
        try {
            closePool(objectPoolGray);
            closePool(objPoolWhite);
            closePool(objPoolAll);
            closePool(objPoolHighPriority);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }


    public RegistryClient getClient() {
        return client;
    }

    public void setClient(RegistryClient client) {
        this.client = client;
    }
}
