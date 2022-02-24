package com.hqy.rpc;

import com.facebook.swift.service.ThriftService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.fundation.common.base.lang.exception.RpcException;
import com.hqy.fundation.common.base.project.UsingIpPort;
import com.hqy.fundation.common.base.project.UsingIpPortEx;
import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.rpc.nacos.NacosClientManager;
import com.hqy.rpc.nacos.RegistryClient;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.rpc.thrift.DynamicInvocationHandler;
import com.hqy.rpc.thrift.InvokeCallback;
import com.hqy.rpc.thrift.ex.ThriftRpcHelper;
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
 * @author qy
 * @date 2021-08-13 9:54
 */
@Slf4j
public class RpcClient {

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
    private static final Cache<String, DynamicInvocationHandler> MASTER_SERVICE_HANDLERMAP =
            CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();


    /**
     * 获取代理的远程服务对象
     * @param distinguish 是否区分灰白度
     * @param consumer 消费者节点， 请求的发起方，可能为null（不区分灰白度的场景）
     * @param producer 生产者节点， 消费的提供者，可能为null， null表示负载均衡的用法。 不为null表示直连的场景 （即调用特定的producer）
     * @param serviceClass 调用服务的class （注册的服务的interface名称）
     * @param callback 异步回调
     * @return 远程服务对象
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
    protected static <T> T getProxyService(boolean distinguish, final ClusterNode consumer, final ClusterNode producer,
                                           final Class<T> serviceClass, InvokeCallback  callback) throws RpcException {
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.debug("@@@ RpcClient.getProxyService, distinguish:{}, consumer:{}, producer:{}, serviceClass:{}", distinguish, consumer, producer, serviceClass);
        }

        //check param.
        if (Objects.isNull(serviceClass)) {
            throw new IllegalArgumentException("@@@ Parameter serviceClass can not be null.");
        }
        boolean distinguishCheck =  distinguish && (Objects.isNull(consumer) || Objects.isNull(producer));
        if (distinguishCheck) {
            throw new IllegalArgumentException("@@@ Parameter consumer and producer can not all be empty with distinguish mode.");
        }
        String interfaceName = serviceClass.getName();
        DynamicInvocationHandler<T> handler;
        if (Objects.isNull(producer)) {
            handler = CLASS_HANDLER_MAP.get(interfaceName);
            if (Objects.isNull(handler)) {
                String value = checkAnnotation(serviceClass);
                RegistryClient client = NacosClientManager.getNacosClient(value);
                if (Objects.isNull(client)) {
                    throw new RpcException(value + "rpc interface not registry, check interface annotation: className:" + interfaceName);
                } else {
                    //分别获取不同颜色的节点
                    List<ClusterNode> grayNodeList = client.getAllLivingNode(GrayWhitePub.GRAY);
                    List<ClusterNode> whiteNodeList = client.getAllLivingNode(GrayWhitePub.WHITE);
                    List<UsingIpPort> grayUipList = ThriftRpcHelper.convertToUip(grayNodeList);
                    List<UsingIpPort> whiteUipList = ThriftRpcHelper.convertToUip(whiteNodeList);
                    handler = new DynamicInvocationHandler<T>(serviceClass, grayUipList, whiteUipList, callback);
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
