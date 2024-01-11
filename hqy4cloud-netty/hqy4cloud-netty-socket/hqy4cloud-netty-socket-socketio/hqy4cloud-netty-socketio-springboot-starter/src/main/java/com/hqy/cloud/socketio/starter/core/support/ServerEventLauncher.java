package com.hqy.cloud.socketio.starter.core.support;

import com.corundumstudio.socketio.SocketIOServer;
import com.hqy.cloud.socketio.starter.core.AbstractServerLauncher;
import com.hqy.cloud.util.AssertUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @see SocketIOServer launcher.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/23 11:35
 */
public class ServerEventLauncher extends AbstractServerLauncher {
    private static final Logger log = LoggerFactory.getLogger(ServerEventLauncher.class);
    private final Set<EventListener> eventListeners;

    public ServerEventLauncher(boolean enableSsl, boolean randomSession, String keystore, String password, Set<EventListener> eventListeners) {
        super(enableSsl, randomSession, keystore, password);
        this.eventListeners = eventListeners;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void enhanceSocketIoServer(SocketIOServer socketIOServer) throws Exception {
        if (CollectionUtils.isEmpty(this.eventListeners)) {
            log.info("Servet not listener any event.");
        } else {
            for (EventListener eventListener : eventListeners) {
                AssertUtil.notNull(eventListener, "EventListener should not be null.");
                socketIOServer.addEventListener(eventListener.getEventName(),  eventListener.getEventClass(), eventListener.getDataListener());
            }
        }


    }

}
