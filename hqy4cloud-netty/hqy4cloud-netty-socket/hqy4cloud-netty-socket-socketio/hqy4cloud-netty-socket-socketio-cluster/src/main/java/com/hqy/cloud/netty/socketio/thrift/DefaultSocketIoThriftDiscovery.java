package com.hqy.cloud.netty.socketio.thrift;

import cn.hutool.extra.spring.SpringUtil;
import com.hqy.cloud.registry.common.exeception.RegisterDiscoverException;
import com.hqy.cloud.registry.context.ProjectContext;
import com.hqy.cloud.rpc.starter.client.Client;
import com.hqy.cloud.rpc.thrift.service.ThriftSocketIoPushService;
import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.cluster.client.support.SocketClient;
import com.hqy.cloud.util.ProjectExecutors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/12
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultSocketIoThriftDiscovery implements SocketIoThriftDiscovery {
    private final Client client;
    private final SocketClient socketClient;

    @Override
    public <T extends ThriftSocketIoPushService> boolean pushEvent(String applicationName, String bizId, String eventName, String eventJsonData, Class<T> serviceClass, boolean async) {
        T pushService = getSocketIoPushService(applicationName, bizId, serviceClass);
        if (async) {
            pushService.asyncPush(bizId, eventName, eventJsonData);
            return true;
        }
        return pushService.syncPush(bizId, eventName, eventJsonData);
    }

    @Override
    public <T extends ThriftSocketIoPushService> boolean broadcastEvent(String applicationName, String bizId, String eventName, String eventJsonData, Class<T> serviceClass, boolean async) {
        List<SocketServer> allSocketServer = socketClient.getAllSocketServer(applicationName);
        if (CollectionUtils.isEmpty(allSocketServer)) {
            log.warn("Not found socket server {} instance.", applicationName);
            return false;
        }
        if (async) {
            ProjectExecutors.getInstance().execute(() -> doPush(bizId, eventName, eventJsonData, allSocketServer, serviceClass, true));
        } else {
            doPush(bizId, eventName, eventJsonData, allSocketServer, serviceClass, false);
        }
        return true;
    }

    @Override
    public <T extends ThriftSocketIoPushService> boolean pushEvent(String applicationName, Set<String> bizIds, String eventName, String eventJsonData, Class<T> serviceClass, boolean async) {
        Map<String, T> pushServices = getMultipleSocketIoPushService(applicationName, bizIds, serviceClass);
        for (Map.Entry<String, T> entry : pushServices.entrySet()) {
            String bizId = entry.getKey();
            T service = entry.getValue();
            if (async) {
                service.asyncPush(bizId, eventName, eventJsonData);
            } else {
                service.syncPush(bizId, eventName, eventJsonData);
            }
        }
        return true;
    }

    @Override
    public <T extends ThriftSocketIoPushService> boolean pushEvent(String applicationName, Map<String, String> messages, String eventName, Class<T> serviceClass, boolean async) {
        Set<String> bizIds = messages.keySet();
        Map<String, T> pushServices = getMultipleSocketIoPushService(applicationName, bizIds, serviceClass);
        for (Map.Entry<String, T> entry : pushServices.entrySet()) {
            String bizId = entry.getKey();
            T service = entry.getValue();
            String message = messages.get(bizId);
            if (async) {
                service.asyncPush(bizId, eventName, message);
            } else {
                service.syncPush(bizId, eventName, message);
            }
        }
        return true;
    }

    @Override
    public <T extends ThriftSocketIoPushService> boolean broadcastEvent(String applicationName, Set<String> bizIds, String eventName, String eventJsonData, Class<T> serviceClass, boolean async) {
        List<SocketServer> allSocketServer = socketClient.getAllSocketServer(applicationName);
        if (CollectionUtils.isEmpty(allSocketServer)) {
            log.warn("Not found socket server {} instance.", applicationName);
            return false;
        }
        if (async) {
            ProjectExecutors.getInstance().execute(() -> bizIds.forEach(bizId -> doPush(bizId, eventName, eventJsonData, allSocketServer, serviceClass, true)));
        } else {
            bizIds.forEach(bizId -> doPush(bizId, eventName, eventJsonData, allSocketServer, serviceClass, true));
        }
        return true;
    }

    @Override
    public <T extends ThriftSocketIoPushService> boolean broadcastAll(String applicationName, String eventName, String eventJsonData, Class<T> serviceClass, boolean async) {
        List<SocketServer> allSocketServer = socketClient.getAllSocketServer(applicationName);
        if (CollectionUtils.isEmpty(allSocketServer)) {
            log.warn("Not found socket server {} instance.", applicationName);
            return false;
        }
        return false;
    }

    @Override
    public <T extends ThriftSocketIoPushService> T getSocketIoPushService(String applicationName, String bizId, Class<T> serviceClass) {
        SocketServer socketServer = socketClient.findSocketServer(applicationName, bizId);
        if (socketServer == null) {
            throw new RegisterDiscoverException("Not found socket server by " + applicationName +  " , bizId " + bizId);
        }
        String socketServerAddress = socketServer.getAddress();
        if (ProjectContext.getContextInfo().isLocalFactor(socketServerAddress, applicationName)) {
            return SpringUtil.getBean(serviceClass);
        }
        return client.getRemoteService(serviceClass, socketServerAddress);
    }

    @Override
    public <T extends ThriftSocketIoPushService> Map<String, T> getMultipleSocketIoPushService(String applicationName, Set<String> bizIds, Class<T> serviceClass) {
        Map<String, SocketServer> socketServers = socketClient.findSocketServers(applicationName, bizIds);
        if (MapUtils.isEmpty(socketServers) || socketServers.values().stream().anyMatch(Objects::isNull)) {
            throw new RegisterDiscoverException("Not found socket servers by " + applicationName +  " , bizIds " + bizIds);
        }
        Map<String, T> cache = new HashMap<>(bizIds.size());
        Map<String, T> resultMap = new HashMap<>(bizIds.size());
        for (String bizId : bizIds) {
            SocketServer socketServer = socketServers.get(bizId);
            String address = socketServer.getAddress();
            T service = cache.computeIfAbsent(address, t -> client.getRemoteService(serviceClass, address));
            resultMap.put(bizId, service);
        }
        return resultMap;
    }


    private <T extends ThriftSocketIoPushService> void doPushAll(String eventName, String eventJsonData, List<SocketServer> allSocketServer, Class<T> serviceClass, boolean async) {
        for (SocketServer socketServer : allSocketServer) {
            String address = socketServer.getAddress();
            T service = client.getRemoteService(serviceClass, address);
            if (async) {
                service.asyncBroadcast(eventName, eventJsonData);
            } else {
                service.syncBroadcast(eventName, eventJsonData);
            }

        }
    }

    private <T extends ThriftSocketIoPushService> void doPush(String bizId, String eventName, String eventJsonData, List<SocketServer> allSocketServer, Class<T> serviceClass, boolean async) {
        for (SocketServer socketServer : allSocketServer) {
            String address = socketServer.getAddress();
            T service = client.getRemoteService(serviceClass, address);
            if (async) {
                service.asyncPush(bizId, eventName, eventJsonData);
            } else {
                service.syncPush(bizId, eventName, eventJsonData);
            }
        }
    }
}
