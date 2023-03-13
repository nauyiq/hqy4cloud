/**
 * Copyright (c) 2012-2019 Nikita Koksharov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.corundumstudio.socketio.handler;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.namespace.Namespace;
import com.corundumstudio.socketio.namespace.NamespacesHub;
import com.corundumstudio.socketio.scheduler.CancelableScheduler;
import com.corundumstudio.socketio.scheduler.SchedulerKey;
import com.hqy.cloud.common.base.lang.NumberConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import io.netty.channel.Channel;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ClientsBox {

    private static final Logger log = LoggerFactory.getLogger(ClientsBox.class);

    private final Map<UUID, ClientHead> uuid2clients = PlatformDependent.newConcurrentHashMap();
    private final Map<Channel, ClientHead> channel2clients = PlatformDependent.newConcurrentHashMap();

    // TODO use storeFactory
    public HandshakeData getHandshakeData(UUID sessionId) {
        ClientHead client = uuid2clients.get(sessionId);
        if (client == null) {
            return null;
        }

        return client.getHandshakeData();
    }

    public void addClient(ClientHead clientHead) {
        uuid2clients.put(clientHead.getSessionId(), clientHead);
    }

    public void addClient(ClientHead client, CancelableScheduler disconnectScheduler, Configuration configuration, NamespacesHub namespacesHub) {
        try {
            UUID sessionId = client.getSessionId();
            HandshakeData handshakeData = client.getHandshakeData();
            String bizId = handshakeData.getBizId();
            uuid2clients.put(sessionId, client);
            SchedulerKey key = new SchedulerKey(SchedulerKey.Type.POLLING_AUTH_WEBSOCKET_TIMEOUT, sessionId);
            disconnectScheduler.schedule(key, () -> {
                log.warn("@@@ polling upgrade auth过期 {}, {}", sessionId, bizId);
                // 监听某uuid经过auth认证，但长时间没有去websocket，产生的泄漏
                uuid2clients.remove(sessionId);

                // 场景：兼容无效连接请求没有upgrade的情况，没有进行到升级websocket导致的 allClients泄漏。
                if (CommonSwitcher.ENABLE_NAMESPACE_CLIENTS_LEAK_PROTECTION.isOn()) {
                    try {
                        Namespace namespace = namespacesHub.get(Namespace.DEFAULT_NAME);
                        namespace.removeSocketIoClients(sessionId);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                //两分钟一次
                // 注意：不用处理channel2clients ，此是由channel生命周期自动管理
            }, NumberConstants.ONE_MINUTES_4MILLISECONDS * 2, TimeUnit.MILLISECONDS);


        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void removeClient(UUID sessionId) {
        ClientHead clientHead = uuid2clients.remove(sessionId);
        if (Objects.isNull(clientHead)) {
            log.warn("@@@ ClientHead already remove or disconnect. {}", sessionId);
        } else {
            //移除业务拓展 如果握手信息中有一个参数 bizId,需要同时剔除 bizId2UUIDMap
            HandshakeData handshakeData = clientHead.getHandshakeData();
            if (Objects.nonNull(handshakeData)) {
                String bizId = handshakeData.getBizId();
                ClientsBoxEx.getInstance().removeClient(sessionId, bizId);
            }
        }
    }

    public ClientHead get(UUID sessionId) {
        return uuid2clients.get(sessionId);
    }

    public void add(Channel channel, ClientHead clientHead) {
        channel2clients.put(channel, clientHead);
    }

    public void remove(Channel channel) {
        channel2clients.remove(channel);
    }


    public ClientHead get(Channel channel) {
        return channel2clients.get(channel);
    }


}
