package com.hqy.netty.websocket.session;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/9 9:47
 */
public class ChannelWsSessionManager {

    private ChannelWsSessionManager() {}

    /**
     * key    channel的id
     * value 当前通道的会话
     */
    private static final Map<String, BaseWsSession> SESSION_MAP = new HashMap<>();

    public static Map<String, BaseWsSession> getSessionMap() {
        return SESSION_MAP;
    }

    public static BaseWsSession getSession(String channelId) {
        return SESSION_MAP.get(channelId);
    }

    public static void registry(String channelId, BaseWsSession wsSession) {
        SESSION_MAP.put(channelId, wsSession);
    }

    public static BaseWsSession cancel(String channelId) {
        return SESSION_MAP.remove(channelId);
    }


}
