package com.hqy.cloud.netty.socketio.thrift;

import cn.hutool.core.map.MapUtil;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.ex.SocketIoUtil;
import com.hqy.cloud.netty.socketio.SocketIoSocketServer;
import com.hqy.cloud.rpc.thrift.service.ThriftSocketIoPushService;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.ProjectExecutors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/12
 */
@Slf4j
public abstract class AbstractThriftSocketIoPushService implements ThriftSocketIoPushService {
    private final SocketIoSocketServer socketServer;
    protected AbstractThriftSocketIoPushService(SocketIoSocketServer socketServer) {
        this.socketServer = socketServer;
    }

    @Override
    public boolean syncPush(String bizId, String eventName, String wsMessageJson) {
        try {
            return SocketIoUtil.doPush(bizId, eventName, wsMessageJson, socketServer.getSocketIOServer());
        } catch (Exception e) {
            log.error("Failed execute to syncPush. bizId: {} | eventName: {}, wsMessageJson:{}.", bizId, eventName, wsMessageJson, e);
            return false;
        }
    }

    @Override
    public boolean syncPushMultiple(Set<String> bizIdSet, String eventName, String wsMessageJson) {
        try {
            boolean happenError = false;
            for (String bizId : bizIdSet) {
                try {
                    SocketIoUtil.doPush(bizId, eventName, wsMessageJson, socketServer.getSocketIOServer());
                } catch (Throwable cause) {
                    AssertUtil.isTrue(happenError, "syncPushMultiple consecutive errors, cause " + cause.getMessage());
                    happenError = true;
                    log.error("Failed execute to syncPushMultiple. bizId: {} | eventName: {}, wsMessageJson:{}.", bizId, eventName, wsMessageJson, cause);
                }
            }
        } catch (Throwable cause) {
            log.error(cause.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean syncPushMultiples(String eventName, Map<String, String> messageMap) {
        if (MapUtil.isEmpty(messageMap)) {
            return false;
        }
        for (Map.Entry<String, String> entry : messageMap.entrySet()) {
            String bizId = entry.getKey();
            String message = entry.getValue();
            try {
                SocketIoUtil.doPush(bizId, eventName, message, socketServer.getSocketIOServer());
            } catch (Throwable cause) {
                log.warn("Failed execute to dp push by syncPushMultiples, bizId: {}.", bizId);
            }
        }
        return true;
    }

    @Override
    public boolean syncBroadcast(String eventName, String wsMessageJson) {
        if (StringUtils.isBlank(eventName)) {
            log.warn("Sync broadcast message should not be empty.");
            return false;
        }
        SocketIOServer socketIOServer = socketServer.getSocketIOServer();
        SocketIoUtil.doPushAll(eventName, wsMessageJson, socketIOServer);
        return true;
    }

    @Override
    public void asyncPush(String bizId, String eventName, String wsMessageJson) {
        ProjectExecutors.getInstance().execute(() -> this.syncPush(bizId, eventName, wsMessageJson));
    }

    @Override
    public void asyncPushMultiple(Set<String> bizIdSet, String eventName, String wsMessageJson) {
        ProjectExecutors.getInstance().execute(() -> this.syncPushMultiple(bizIdSet, eventName, wsMessageJson));
    }

    @Override
    public void asyncPushMultiples(String eventName, Map<String, String> messageMap) {
        ProjectExecutors.getInstance().execute(() -> this.syncPushMultiples(eventName, messageMap));
    }

    @Override
    public void asyncBroadcast(String eventName, String wsMessageJson) {
        ProjectExecutors.getInstance().execute(() -> this.syncBroadcast(eventName, wsMessageJson));
    }
}
