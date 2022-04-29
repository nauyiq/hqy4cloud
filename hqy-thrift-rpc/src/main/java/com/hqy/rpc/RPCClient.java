package com.hqy.rpc;

import com.facebook.swift.service.ThriftService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.base.common.base.lang.ActuatorNodeEnum;
import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.base.common.base.project.UsingIpPort;
import com.hqy.base.common.base.project.UsingIpPortEx;
import com.hqy.base.common.rpc.api.RPCService;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.rpc.nacos.NacosClientManager;
import com.hqy.rpc.nacos.RegistryClient;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.rpc.route.ConfigCenterDirectServer;
import com.hqy.rpc.thrift.handler.DynamicInvocationHandler;
import com.hqy.rpc.thrift.InvokeCallback;
import com.hqy.rpc.thrift.ex.ThriftRpcHelper;
import com.hqy.util.AssertUtil;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * RPC服务的客户端, 用于远程调用
 * 本RMIClient对外通过暴露 getRemoteService 来提供RMI服务
 * <1>并且内置了灰度模式：灰色节点可以调用灰色和白色节点服务（灰色 -> 灰色, 白色） 白色节点只能调用白色节点服务（白色 -> 白色）
 * <2>高优先级同IP同环卡策略: rpc节点调用节点时, 优先调用同一ip下的服务
 * @author qiyuan.honh
 * @date 2021-08-13 9:54
 */
@Slf4j
public class RPCClient {

    /**
     *  key: rpc接口的class name
     *  value: 对应的动态代理handler
     */
    @SuppressWarnings("rawtypes")
    private static final Map<String, DynamicInvocationHandler> CLASS_HANDLER_MAP = new ConcurrentHashMap<>();


    /**
     * 直连模式
     * key： UsingIpPortEx (目标节点+service)
     * value：对应的动态代理handler
     */
    @SuppressWarnings("rawtypes")
    private static final Map<UsingIpPortEx, DynamicInvocationHandler> DIRECT_HANDLER_MAP = new ConcurrentHashMap<>();


    /**
     * 主节点模式
     * key: 模块名称
     * value： 对应的主节点的动态代理handler
     */
    @SuppressWarnings("rawtypes")
    private static final Cache<String, DynamicInvocationHandler> MASTER_SERVICE_HANDLER_MAP =
            CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();



    /**
     * 不区分灰度白度 节点个数计算
     * @param serviceClass RPCService
     * @return 节点实例个数
     */
    public static int countClusterNode(Class<? extends RPCService> serviceClass) {
        String value = checkAnnotation(serviceClass);
        RegistryClient nacosClient = NacosClientManager.getNacosClient(value);
        if (Objects.isNull(nacosClient)) {
            throw new RpcException("Service not registry. check interface annotation, className =" + serviceClass.getName() + ",moduleName:" + value);
        }
        return nacosClient.countNodes();
    }


    /**
     * 不区分灰度白度 节点个数计算
     * @param serviceNameEn 服务英文名
     * @return 节点实例个数
     */
    public static int countClusterNode(String serviceNameEn) {
        AssertUtil.notEmpty(serviceNameEn, "@@@ Service english name can not be null.");
        RegistryClient client = NacosClientManager.getNacosClient(serviceNameEn);
        if (Objects.isNull(client)) {
            throw new RpcException("Service not registry. check interface annotation, moduleName:" + serviceNameEn);
        }
        return client.countNodes();
    }

    /**
     * 区分灰度白度 节点个数计算
     * @param serviceClass RPCService
     * @param pub 节点颜色
     * @return 节点实例个数
     */
    public static int countClusterNode(Class<? extends RPCService> serviceClass, GrayWhitePub pub) {
        String serviceNameEn = checkAnnotation(serviceClass);
        RegistryClient client = NacosClientManager.getNacosClient(serviceNameEn);
        if (Objects.isNull(client)) {
            throw new RpcException("Service not registry. check interface annotation, className = " + serviceClass.getName() + ", moduleName = " + serviceNameEn);
        }
        return client.countNodes(pub);
    }

    /**
     * 区分灰度白度 节点个数计算
     * @param serviceNameEn 服务英文名
     * @param pub 节点颜色
     * @return 节点实例个数
     */
    public static int countClusterNode(String serviceNameEn, GrayWhitePub pub) {
        AssertUtil.notEmpty(serviceNameEn, "@@@ Service english name can not be null.");
        RegistryClient client = NacosClientManager.getNacosClient(serviceNameEn);
        if (Objects.isNull(client)) {
            throw new RpcException("Service not registry. check interface annotation, className = moduleName:" + serviceNameEn);
        }
        return client.countNodes(pub);
    }


    public static <T> T getRemoteService(Class<T> serviceClass) {
        return getRemoteService(serviceClass, ThriftRpcHelper.DEFAULT_HASH_FACTOR);
    }

    public static <T> T getRemoteService(Class<T> serviceClass, String hashFactor) {
        return getRemoteService(serviceClass, hashFactor, null);
    }

    public static <T> T getRemoteService(Class<T> serviceClass, InvokeCallback callback) {
        return getRemoteService(serviceClass, ThriftRpcHelper.DEFAULT_HASH_FACTOR, callback);
    }


    /**
     * 获取远程服务: 适用于RPC接口暴露的所有服务 可以当做本地方法一样调用
     * 内部支持直连模式 方便开发时debug 直连服务
     * 内部支持灰度模式 根据上文下和consumer节点信息 判断是否执行灰度策略
     * @param serviceClass RPCService
     * @param hashFactor 哈希因子
     * @param callback 回调方法
     * @return 远程服务对象
     */
    public static <T> T getRemoteService(Class<T> serviceClass, String hashFactor, InvokeCallback callback) {
        //获取服务名
        String serviceNameEn = checkAnnotation(serviceClass);
        if (ThriftRpcHelper.DEFAULT_HASH_FACTOR.equals(hashFactor)) {
            //默认的hash因子
            boolean isDirectService = false;
            try {
                ConfigCenterDirectServer directServer = SpringContextHolder.getBean(ConfigCenterDirectServer.class);
                if (EnvironmentConfig.getInstance().enableRpcDirect()) {
                    //判断当前服务是否配置直连服务
                    isDirectService = directServer.isDirect(serviceNameEn);
                }
                if (isDirectService) {
                    UsingIpPort directUip = directServer.getDirectUip(serviceNameEn);
                    if(Objects.nonNull(directUip)) {
                        //有直连节点数据
                        log.info("@@@ 调用直连配置的RPC服务:{}, 接口:{}", directUip, serviceClass);
                        return getDirectedService(null, directUip, serviceClass, callback);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            //如果哈希因子不是default，需要从节点中根据hash因子选一个来直连
            UsingIpPort targetProducer = routeByHashFactor(hashFactor, serviceNameEn, serviceClass);
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.info("@@@ 根据hash因子调用远程服务, hashFactor = {}, serviceName:{}, serviceClass:{}, uip:{}", hashFactor, serviceNameEn,
                        serviceClass.getName(), targetProducer);
            }
            return getDirectedService(null, targetProducer, serviceClass, callback);
        }

        //调用注册中心的远程服务.非直连也非hash因子获取的远程服务.
        T service;
        boolean grayMode = false;
        ClusterNode consumer = null;
        ProjectContextInfo contextInfo = SpringContextHolder.getProjectContextInfo();
        if (CommonSwitcher.ENABLE_GRAY_MECHANISM.isOn() && Objects.nonNull(contextInfo)) {
            consumer = new ClusterNode();
            Integer pubValue = contextInfo.getPubValue();
            if (pubValue.equals(GrayWhitePub.GRAY.value) || pubValue.equals(GrayWhitePub.WHITE.value)) {
                consumer.setPubValue(pubValue);
                grayMode = true;
            } else {
                log.warn("@@@ Unknown projectContextInfo.pubValue, value = {}", pubValue);
            }
        }

        if (grayMode) {
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.debug("@@@ Gray mode call serviceRpc -> {}", serviceClass);
            }
            service = getProxyService(true, consumer, null, serviceClass, callback);
        } else {
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.debug("@@@ Not gray mode call serviceRpc -> {}", serviceClass);
            }
            service = getProxyService(false, null, null, serviceClass, callback);
        }

        return service;
    }

    /**
     * 根据hash因子路由到相应的节点
     * @param hashFactor hash因子
     * @param serviceNameEn 服务名
     * @param serviceClass RPCService
     * @return 远程服务节点
     */
    private static <T> UsingIpPort routeByHashFactor(String hashFactor, String serviceNameEn, Class<T> serviceClass) {
        UsingIpPort targetProducer = ThriftRpcHelper.convertHash(hashFactor);
        if (Objects.nonNull(targetProducer)) {
            //表示当前hash因子是ip:rpcPort的格式 直接return
            return targetProducer;
        }
        RegistryClient nacosClient = NacosClientManager.getNacosClient(serviceNameEn);
        if (Objects.isNull(nacosClient)) {
            throw new RpcException("Service not registry. check interface annotation, className = " + serviceClass.getName() + ", moduleName =" + serviceNameEn);
        } else {
            targetProducer = nacosClient.pickupHashFactor(serviceNameEn, hashFactor);
            if (Objects.isNull(targetProducer)) {
                log.error("@@@ No hashFactor node for module, {} , {}", hashFactor, serviceNameEn);
                throw new IllegalStateException("No hashFactor node for module:" + serviceNameEn + ", hashFactor:" + hashFactor);
            }
        }
        return targetProducer;
    }

    /**
     * 获取代理的远程服务对象
     * @param distinguish 是否区分灰白度
     * @param consumer 消费者节点， 请求的发起方，可能为null（不区分灰白度的场景）
     * @param producer 生产者节点， 消费的提供者，可能为null， null表示负载均衡的用法。 不为null表示直连的场景 （即调用特定的producer）
     * @param serviceClass 调用服务的class （注册的服务的interface名称）
     * @param callback 异步回调
     * @return 远程服务对象
     */
    @SuppressWarnings({ "unchecked", "deprecation"})
    protected static <T> T getProxyService(boolean distinguish, final ClusterNode consumer, final ClusterNode producer,
                                           final Class<T> serviceClass, InvokeCallback  callback) throws RpcException {
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.debug("@@@ RpcClient.getProxyService, distinguish:{}, consumer:{}, producer:{}, serviceClass:{}", distinguish, consumer, producer, serviceClass);
        }

        //check param.
        if (Objects.isNull(serviceClass)) {
            throw new IllegalArgumentException("@@@ Parameter serviceClass can not be null.");
        }
        boolean distinguishCheck = distinguish && (Objects.isNull(consumer) || Objects.isNull(producer));
        if (distinguishCheck) {
            throw new IllegalArgumentException("@@@ Parameter consumer and producer can not all be empty with distinguish mode.");
        }

        //rpc接口名
        String interfaceName = serviceClass.getName();
        //服务代理handler
        DynamicInvocationHandler<T> handler;

        //生产者producer为空, 说明不是直连模式
        if (Objects.isNull(producer)) {
            handler = CLASS_HANDLER_MAP.get(interfaceName);
            if (Objects.isNull(handler)) {
                String value = checkAnnotation(serviceClass);
                RegistryClient client = NacosClientManager.getNacosClient(value);
                if (Objects.isNull(client)) {
                    throw new RpcException(value + "rpc interface not registry, check interface annotation: className:" + interfaceName);
                } else {
                    //分别获取不同颜色的节点 重新创建target服务的代理handler对象.
                    List<UsingIpPort> grayUipList = ThriftRpcHelper.convertToUip(client.getAllLivingNode(GrayWhitePub.GRAY));
                    List<UsingIpPort> whiteUipList = ThriftRpcHelper.convertToUip(client.getAllLivingNode(GrayWhitePub.WHITE));
                    handler = new DynamicInvocationHandler<>(serviceClass, grayUipList, whiteUipList, callback);
                    handler.setClient(client);

                    //添加观察者 监听nacos节点变化事件
                    client.addNodeActivityObserver(handler);
                    CLASS_HANDLER_MAP.put(interfaceName, handler);
                }
            }
        } else {
            //直连模式
            UsingIpPortEx uipEx = new UsingIpPortEx(producer.getUip(), interfaceName);
            handler = DIRECT_HANDLER_MAP.get(uipEx);
            if (Objects.isNull(handler)) {
                List<UsingIpPort> usingIpPorts = Collections.singletonList(producer.getUip());
                handler = new DynamicInvocationHandler<>(serviceClass, usingIpPorts, usingIpPorts, callback);
                DIRECT_HANDLER_MAP.put(uipEx, handler);
            }
        }

        //JDK 动态代理
        Object obj = Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, handler);
        return (T) obj;
    }


    /**
     * 获取代理的远程服务对象。远程服务提供者ip 端口由调用者指定
     * 适用于直连模式
     * @param consumer 消费者节点 可能为null
     * @param producer 生产者ip
     * @param serviceClass 调用的服务Class（注册的服务接口interface的名称）
     * @param callback 回调
     * @return 远程服务对象
     */
    protected static <T> T getDirectedService( final ClusterNode consumer, final UsingIpPort producer,
                                               Class<T> serviceClass, InvokeCallback callback) throws RpcException{
        AssertUtil.isTrue(Objects.isNull(producer) || Objects.isNull(serviceClass),
                "@@@ Direct rpc service failure, pram producer or serviceClass is null");
        ClusterNode producerNode = new ClusterNode();
        producerNode.setUip(producer);
        producerNode.setName("直连服务");
        producerNode.setNameEn("direct service");
        producerNode.setActuatorNode(ActuatorNodeEnum.PROVIDER);
        return getProxyService(false, consumer, producerNode, serviceClass, callback);
    }


    /**
     * 检查 @ThriftService 并且返回注解的value（节点服务名称）
     * @param service rpc接口
     * @return 节点服务名称
     */
    protected static String checkAnnotation(Class<?> service) {
        ThriftService thriftService = service.getAnnotation(ThriftService.class);
        if (Objects.isNull(thriftService)) {
            throw new RpcException("@@@ Only ThriftService supported, class:" + service.getSimpleName());
        }
        String value = thriftService.value();
        if (StringUtils.isBlank(value)) {
            throw new RpcException("@@@ @ThriftService Annotation value not specified, class:" + service.getSimpleName());
        }
        return value;
    }


}
