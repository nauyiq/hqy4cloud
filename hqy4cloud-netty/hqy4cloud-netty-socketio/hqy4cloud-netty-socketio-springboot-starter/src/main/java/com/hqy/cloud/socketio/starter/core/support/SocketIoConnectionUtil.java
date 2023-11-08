package com.hqy.cloud.socketio.starter.core.support;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.foundation.common.route.LoadBalanceHashFactorManager;
import com.hqy.cloud.foundation.common.route.SocketClusterStatus;
import com.hqy.cloud.foundation.common.route.SocketClusterStatusManager;
import com.hqy.cloud.rpc.core.Environment;
import com.hqy.cloud.rpc.nacos.client.RPCClient;
import com.hqy.cloud.rpc.thrift.service.ThriftSocketIoPushService;
import com.hqy.cloud.util.IpUtil;
import com.hqy.cloud.util.config.ConfigurationContext;
import com.hqy.cloud.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.hqy.cloud.common.base.config.ConfigConstants.SOCKET_CONNECTION_HOST;
import static com.hqy.cloud.util.config.ConfigurationContext.PropertiesEnum.SERVER_PROPERTIES;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/26 13:44
 */
@Slf4j
public class SocketIoConnectionUtil {

    public static String getSocketHost(int port) {
        String host = ConfigurationContext.getProperty(SERVER_PROPERTIES, SOCKET_CONNECTION_HOST);
        if (StringUtils.isBlank(host)) {
            if (StringUtils.isBlank(host) && Environment.getInstance().isDevEnvironment()) {
                // Dev env using ip.
                host = StringConstants.Host.HTTP + IpUtil.getHostAddress();
                host = CommonSwitcher.ENABLE_GATEWAY_SOCKET_AUTHORIZE.isOn() ? host + StrUtil.COLON + 9527 : host + StrUtil.COLON + port;
                return host;
            } else {
                return StringConstants.Host.HTTPS_API_GATEWAY;
            }
        }
        return host + StrUtil.COLON + port;
    }

    public static <T> T getSocketIoPushService(String bizId, Class<T> serviceClass, String serviceName) {
        SocketClusterStatus query = SocketClusterStatusManager.query(Environment.getInstance().getEnvironment(), serviceName);
        if (query.isEnableMultiWsNode()) {
            //开启了集群. 获取当前bizId所在的路由表位置
            int hash = query.getSocketIoPathHashMod(bizId);
            //获取对应服务的路由因子
            String factor = LoadBalanceHashFactorManager.queryHashFactor(serviceName, hash);
            if (!SpringContextHolder.getProjectContextInfo().isLocalFactor(factor, serviceName)) {
                return RPCClient.getRemoteService(serviceClass, serviceName);
            }
        }
        return SpringContextHolder.getBean(serviceClass);
    }

    public static <T> Map<String, T> getMultipleSocketIoPushService(Set<String> bizIds, Class<T> serviceClass, String serviceName) {
        SocketClusterStatus query = SocketClusterStatusManager.query(Environment.getInstance().getEnvironment(), serviceName);
        if (query.isEnableMultiWsNode()) {
            Map<String, T> resultMap = MapUtil.newHashMap();
            // query bizId of hash value
            Map<String, Integer> map = bizIds.parallelStream().collect(Collectors.toMap(bizId -> bizId, query::getSocketIoPathHashMod));
            List<Integer> hashList = map.values().stream().distinct().toList();
            Map<Integer, String> hashValueMap = LoadBalanceHashFactorManager.queryHashFactorMap(serviceName, hashList);
            Map<Integer, T> tMap = hashValueMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                String hashFactor = entry.getValue();
                if (!SpringContextHolder.getProjectContextInfo().isLocalFactor(hashFactor, serviceName)) {
                    return RPCClient.getRemoteService(serviceClass, serviceName);
                } else {
                    return SpringContextHolder.getBean(serviceClass);
                }
            }));
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String bizId = entry.getKey();
                Integer hash = entry.getValue();
                T service = tMap.getOrDefault(hash, SpringContextHolder.getBean(serviceClass));
                resultMap.put(bizId, service);
            }
            return resultMap;
        }
        return null;

    }

    public static boolean doPrivateMessage(boolean async, String serviceName, String to, String eventName, String messagePayload) {
        ThriftSocketIoPushService socketIoPushService = getSocketIoPushService(to, ThriftSocketIoPushService.class, serviceName);
        try {
            if (async) {
                socketIoPushService.asyncPush(to, eventName, messagePayload);
                return true;
            } else {
                return socketIoPushService.syncPush(to, eventName, messagePayload);
            }
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return false;
        }
    }

    public static boolean doBroadcastMessages(boolean async, String serviceName, Set<String> broadcastUsers, String eventName, String messagePayload) {
        try {
            SocketClusterStatus query = SocketClusterStatusManager.query(Environment.getInstance().getEnvironment(), serviceName);
            if (query.isEnableMultiWsNode()) {
                // query bizId of hash value of map. key: bizId | value: hash
                Map<String, Integer> hashMap = broadcastUsers.parallelStream().collect(Collectors.toMap(bizId -> bizId, query::getSocketIoPathHashMod));
                // query hashFactor and ThriftSocketIoPushService  map.
                Map<Integer, String> hashFactorMap = LoadBalanceHashFactorManager.queryHashFactorMap(serviceName, hashMap.values().stream().distinct().toList());
                // group by hash
                Map<Integer, List<String>> groupByHash = MapUtil.newHashMap();
                for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
                    String bizId = entry.getKey();
                    Integer hash = entry.getValue();
                    List<String> absent = groupByHash.computeIfAbsent(hash, v -> new ArrayList<>());
                    absent.add(bizId);
                }
                // do broadcast.
                for (Map.Entry<Integer, List<String>> entry : groupByHash.entrySet()) {
                    Integer hash = entry.getKey();
                    List<String> bizIds = entry.getValue();
                    String hashFactor = hashFactorMap.getOrDefault(hash, StringConstants.DEFAULT);
                    ThriftSocketIoPushService pushService;
                    if (SpringContextHolder.getProjectContextInfo().isLocalFactor(hashFactor, serviceName)) {
                        pushService = SpringContextHolder.getBean(ThriftSocketIoPushService.class);
                    } else {
                        // using rpc service.
                        pushService = RPCClient.getRemoteService(ThriftSocketIoPushService.class);
                    }
                    if (async) {
                        pushService.asyncPushMultiple(new HashSet<>(bizIds), eventName, messagePayload);
                    } else {
                        pushService.syncPushMultiple(new HashSet<>(bizIds), eventName, messagePayload);
                    }
                    return true;
                }
            } else {
                ThriftSocketIoPushService pushService = SpringContextHolder.getBean(ThriftSocketIoPushService.class);
                if (async) {
                    pushService.asyncPushMultiple(broadcastUsers, eventName, messagePayload);
                } else {
                    return pushService.syncPushMultiple(broadcastUsers, eventName, messagePayload);
                }
            }
            return true;
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return false;
        }
    }

    public static boolean doMultiplePushMessages(boolean async, String serviceName, String eventName, Map<String, String> userMap) {
        try {
            SocketClusterStatus query = SocketClusterStatusManager.query(Environment.getInstance().getEnvironment(), serviceName);
            if (!query.isEnableMultiWsNode()) {
                ThriftSocketIoPushService pushService = SpringContextHolder.getBean(ThriftSocketIoPushService.class);
                if (async) {
                    pushService.asyncPushMultiples(eventName, userMap);
                } else {
                    pushService.syncPushMultiples(eventName, userMap);
                }
            } else {
                // query bizId of hash value of map. key: bizId | value: hash
                Map<String, Integer> hashMap = userMap.keySet().parallelStream().collect(Collectors.toMap(bizId -> bizId, query::getSocketIoPathHashMod));
                // query hashFactor and ThriftSocketIoPushService  map.
                Map<Integer, String> hashFactorMap = LoadBalanceHashFactorManager.queryHashFactorMap(serviceName, hashMap.values().stream().distinct().toList());
                // group by hash
                Map<Integer, Map<String, String>> groupByHash = MapUtil.newHashMap();
                for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
                    String bizId = entry.getKey();
                    Integer hash = entry.getValue();
                    Map<String, String> absent = groupByHash.computeIfAbsent(hash, v -> MapUtil.newHashMap());
                    absent.put(bizId, userMap.get(bizId));
                }
                // do broadcast.
                for (Map.Entry<Integer, Map<String, String>> entry : groupByHash.entrySet()) {
                    Integer hash = entry.getKey();
                    Map<String, String> bizIds = entry.getValue();
                    String hashFactor = hashFactorMap.getOrDefault(hash, StringConstants.DEFAULT);
                    ThriftSocketIoPushService pushService;
                    if (SpringContextHolder.getProjectContextInfo().isLocalFactor(hashFactor, serviceName)) {
                        pushService = SpringContextHolder.getBean(ThriftSocketIoPushService.class);
                    } else {
                        // using rpc service.
                        pushService = RPCClient.getRemoteService(ThriftSocketIoPushService.class);
                    }
                    if (async) {
                        pushService.asyncPushMultiples(eventName, bizIds);
                    } else {
                        pushService.asyncPushMultiples(eventName, bizIds);
                    }
                }
            }
            return true;
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return false;
        }


    }







}
