/**
 * Copyright (c) 2012-2019 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hqy.socketio.handler;


import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.socketio.Configuration;
import com.hqy.socketio.DisconnectableHub;
import com.hqy.socketio.HandshakeData;
import com.hqy.socketio.Transport;
import com.hqy.socketio.ack.AckManager;
import com.hqy.socketio.messages.OutPacketMessage;
import com.hqy.socketio.namespace.Namespace;
import com.hqy.socketio.protocol.Packet;
import com.hqy.socketio.protocol.PacketType;
import com.hqy.socketio.scheduler.CancelableScheduler;
import com.hqy.socketio.scheduler.SchedulerKey;
import com.hqy.socketio.store.Store;
import com.hqy.socketio.store.StoreFactory;
import com.hqy.socketio.transport.NamespaceClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.AttributeKey;
import io.netty.util.internal.PlatformDependent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHead {

    private static final Logger log = LoggerFactory.getLogger(ClientHead.class);

    public static final AttributeKey<ClientHead> CLIENT = AttributeKey.<ClientHead>valueOf("client");

    private final AtomicBoolean disconnected = new AtomicBoolean();
    private final Map<Namespace, NamespaceClient> namespaceClients = PlatformDependent.newConcurrentHashMap();
    private final Map<Transport, TransportState> channels = new HashMap<Transport, TransportState>(2);
    private final HandshakeData handshakeData;
    private final UUID sessionId;

    private final Store store;
    private final DisconnectableHub disconnectableHub;
    private final AckManager ackManager;
    private final ClientsBox clientsBox;
    private final CancelableScheduler disconnectScheduler;
    private final Configuration configuration;

    private Packet lastBinaryPacket;
    private final String address;

    // TODO use lazy set
    private volatile Transport currentTransport;

    /**
     * 标识是否已经走到upgrade通道完整径
     */
    private boolean upgraded = false;

    public ClientHead(UUID sessionId, AckManager ackManager, DisconnectableHub disconnectable,
                      StoreFactory storeFactory, HandshakeData handshakeData, ClientsBox clientsBox, Transport transport, CancelableScheduler disconnectScheduler,
                      Configuration configuration, String address) {
        this.sessionId = sessionId;
        this.ackManager = ackManager;
        this.disconnectableHub = disconnectable;
        this.store = storeFactory.createStore(sessionId);
        this.handshakeData = handshakeData;
        this.clientsBox = clientsBox;
        this.currentTransport = transport;
        this.disconnectScheduler = disconnectScheduler;
        this.configuration = configuration;
        //新增源码字段
        this.address = StringUtils.isBlank(address) ? "" : address;

        channels.put(Transport.POLLING, new TransportState());
        channels.put(Transport.WEBSOCKET, new TransportState());
    }

    public void bindChannel(Channel channel, Transport transport) {
        log.debug("binding channel: {} to transport: {}", channel, transport);

        TransportState state = channels.get(transport);
        Channel prevChannel = state.update(channel);
        if (prevChannel != null) {
            clientsBox.remove(prevChannel);
        }
        clientsBox.add(channel, this);

        sendPackets(transport, channel);
    }

    public void releasePollingChannel(Channel channel) {
        TransportState state = channels.get(Transport.POLLING);
        if(channel.equals(state.getChannel())) {
            clientsBox.remove(channel);
            state.update(null);
        }
    }

    public String getOrigin() {
//        return handshakeData.getHttpHeaders().get(HttpHeaderNames.ORIGIN);
        return handshakeData.getOrigin();
    }

    public ChannelFuture send(Packet packet) {
        return send(packet, getCurrentTransport());
    }

    public void cancelPingTimeout() {
        SchedulerKey key = new SchedulerKey(SchedulerKey.Type.PING_TIMEOUT, sessionId);
        disconnectScheduler.cancel(key);
    }

    public void schedulePingTimeout() {
        SchedulerKey key = new SchedulerKey(SchedulerKey.Type.PING_TIMEOUT, sessionId);
        disconnectScheduler.schedule(key, new Runnable() {
            @Override
            public void run() {
                ClientHead client = clientsBox.get(sessionId);
                if (client != null) {
                    client.disconnect();
                    log.debug("{} removed due to ping timeout", sessionId);
                }
            }
        }, configuration.getPingTimeout() + configuration.getPingInterval(), TimeUnit.MILLISECONDS);
    }

    public ChannelFuture send(Packet packet, Transport transport) {
        TransportState state = channels.get(transport);
        state.getPacketsQueue().add(packet);

        Channel channel = state.getChannel();
        if (channel == null
                || (transport == Transport.POLLING && channel.attr(EncoderHandler.WRITE_ONCE).get() != null)) {
            return null;
        }
        return sendPackets(transport, channel);
    }

    private ChannelFuture sendPackets(Transport transport, Channel channel) {
        return channel.writeAndFlush(new OutPacketMessage(this, transport));
    }

    public void removeNamespaceClient(NamespaceClient client) {
        namespaceClients.remove(client.getNamespace());
        if (namespaceClients.isEmpty()) {
            disconnectableHub.onDisconnect(this);
        }
    }

    public NamespaceClient getChildClient(Namespace namespace) {
        return namespaceClients.get(namespace);
    }

    public NamespaceClient addNamespaceClient(Namespace namespace) {
        NamespaceClient client = new NamespaceClient(this, namespace);
        namespaceClients.put(namespace, client);
        return client;
    }

    public Set<Namespace> getNamespaces() {
        return namespaceClients.keySet();
    }

    public boolean isConnected() {
        return !disconnected.get();
    }

    public void onChannelDisconnect() {
        cancelPingTimeout();

        disconnected.set(true);
        for (NamespaceClient client : namespaceClients.values()) {
            client.onDisconnect();
        }
        for (TransportState state : channels.values()) {
            if (state.getChannel() != null) {
                clientsBox.remove(state.getChannel());
            }
        }
    }

    public HandshakeData getHandshakeData() {
        return handshakeData;
    }

    public AckManager getAckManager() {
        return ackManager;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public String getRemoteAddress() {
        return this.address;
    }

    public void disconnect() {
        ChannelFuture future = send(new Packet(PacketType.DISCONNECT));
		if(future != null) {
			future.addListener(ChannelFutureListener.CLOSE);
		}

        onChannelDisconnect();
    }

    public boolean isChannelOpen() {
        for (TransportState state : channels.values()) {
            if (state.getChannel() != null
                    && state.getChannel().isActive()) {
                return true;
            }
        }
        return false;
    }

    public Store getStore() {
        return store;
    }

    public boolean isTransportChannel(Channel channel, Transport transport) {
        TransportState state = channels.get(transport);
        if (state.getChannel() == null) {
            return false;
        }
        return state.getChannel().equals(channel);
    }

    public void upgradeCurrentTransport(Transport currentTransport) {
        TransportState state = channels.get(currentTransport);

        for (Entry<Transport, TransportState> entry : channels.entrySet()) {
            if (!entry.getKey().equals(currentTransport)) {

                Queue<Packet> queue = entry.getValue().getPacketsQueue();
                state.setPacketsQueue(queue);

                sendPackets(currentTransport, state.getChannel());
                this.currentTransport = currentTransport;
                log.debug("Transport upgraded to: {} for: {}", currentTransport, sessionId);
                break;
            }
        }
    }

    /**
     * 兼容websocket协议的直连方式
     * @param namespace Namespace
     */
    public void upgrade(Namespace namespace) {
        if (!upgraded) {
            this.upgraded = true;
            ClientsBoxEx.getInstance().addClient(this.getSessionId(),  handshakeData);
            log.info("@@@ Upgraded通道ok, 即将绑定和通知业务触发: {} for: {}", handshakeData.getBizId(), sessionId);

            NamespaceClient childClient = namespaceClients.get(namespace);
            namespace.onConnect(childClient);

            //场景：容恶意的非法的polling的addClient。如果进行到此“周期”来到，说明websocket成功 。
            if (disconnectScheduler != null && CommonSwitcher.SOCKET_POLLING_HANDSHAKE_DATA_LEAK.isOn()) {
                SchedulerKey key = new SchedulerKey(SchedulerKey.Type.POLLING_AUTH_WEBSOCKET_TIMEOUT, this.getSessionId());
                disconnectScheduler.cancel(key);
            }
        }
    }

    /**
     * 获取业务通道id
     * @return String
     */
    public String getBizId() {
        return handshakeData != null ? handshakeData.getBizId() : "";
    }

    /**
     * 获取客户端真实ip
     * @return String
     */
    public String getClientRealIp() {
        return handshakeData !=null ? handshakeData.getRealIp() : "";
    }


    public Transport getCurrentTransport() {
        return currentTransport;
    }

    public Queue<Packet> getPacketsQueue(Transport transport) {
        return channels.get(transport).getPacketsQueue();
    }

    public void setLastBinaryPacket(Packet lastBinaryPacket) {
        this.lastBinaryPacket = lastBinaryPacket;
    }

    public Packet getLastBinaryPacket() {
        return lastBinaryPacket;
    }


}
