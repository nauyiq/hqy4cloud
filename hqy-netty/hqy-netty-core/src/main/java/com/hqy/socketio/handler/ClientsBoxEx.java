package com.hqy.socketio.handler;

import com.hqy.socketio.HandshakeData;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * ClientBox拓展类
 * 存储重要的内存变量，存了关键了连接的客户端的信息，消息转发或者推送的场合，需要从这里获取远程连接对象
 * @author qiyuan.hong
 * @date 2022-03-18 10:55
 */
public class ClientsBoxEx {

    private static final Logger log = LoggerFactory.getLogger(ClientsBoxEx.class);

    /**
     * 业务ID到UUID（会话id）的映射关系，根据握手信息中是否还有bizId字段自动识别
     */
    private final Map<String, Set<UUID>> bizId2UUIDMap = PlatformDependent.newConcurrentHashMap();

    private final Map<UUID, String> uuid2BizIdMap = PlatformDependent.newConcurrentHashMap();

    private final Map<UUID, HandshakeData> uuid2HandshakeDataMap = PlatformDependent.newConcurrentHashMap();

    private ClientsBoxEx() {
    }

    private static final ClientsBoxEx INSTANCE = new ClientsBoxEx();

    public static ClientsBoxEx getInstance() {
        return INSTANCE;
    }

    public void addClient(UUID sessionId, HandshakeData handshakeData) {
        String bizId  = handshakeData.getBizId();
        if (bizId == null) {
            log.warn("@@@ No bizId for client to add");
        } else {
            uuid2BizIdMap.put(sessionId, bizId);
            Set<UUID> set = bizId2UUIDMap.get(bizId);
            if (set == null) {
                set = new HashSet<>(2);
            }
            set.add(sessionId);
            bizId2UUIDMap.put(bizId, set);
        }
        uuid2HandshakeDataMap.put(sessionId, handshakeData);

    }

    /**
     * 根据bizId 获取UUID；<br>
     * 获取到UUID后，可以通过Namespace的  getClient(UUID uuid) 方法拿到SocketIOClient，继而可以向其发消息！
     * @param bizId 业务id
     * @return Set<UUID>
     */
    public Set<UUID> getUUID(String bizId) {
        if(bizId == null) {
            return null;
        }
        return bizId2UUIDMap.get(bizId);
    }

    /**
     * 移除client
     * @param sessionId 会话id
     * @param bizId 业务id
     */
    public void removeClient(UUID sessionId, String bizId) {
        uuid2BizIdMap.remove(sessionId);
        uuid2HandshakeDataMap.remove(sessionId);
        if (bizId == null) {
            log.warn("@@@ No bizId for client to add");
        } else {
            Set<UUID> set = bizId2UUIDMap.get(bizId);
            if (set == null || set.size() <= 1) {
                // 如果没有或者只有一个客户端连接
                bizId2UUIDMap.remove(bizId);
            } else {
                // 如果超过1个客户端连接，只能断开掉其中一个噢
                set.remove(sessionId);
                bizId2UUIDMap.put(bizId, set);
            }
        }

    }


    public Map<String, Set<UUID>> getBizId2UUIDMap() {
        return bizId2UUIDMap;
    }

    public Map<UUID, String> getUuid2BizIdMap() {
        return uuid2BizIdMap;
    }

    public Map<UUID, HandshakeData> getUuid2HandshakeDataMap() {
        return uuid2HandshakeDataMap;
    }
}
