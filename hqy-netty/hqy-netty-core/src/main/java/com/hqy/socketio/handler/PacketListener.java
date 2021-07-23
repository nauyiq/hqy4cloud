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

import com.hqy.socketio.AckRequest;
import com.hqy.socketio.Transport;
import com.hqy.socketio.ack.AckManager;
import com.hqy.socketio.namespace.Namespace;
import com.hqy.socketio.namespace.NamespacesHub;
import com.hqy.socketio.protocol.Packet;
import com.hqy.socketio.protocol.PacketType;
import com.hqy.socketio.scheduler.CancelableScheduler;
import com.hqy.socketio.scheduler.SchedulerKey;
import com.hqy.socketio.transport.NamespaceClient;
import com.hqy.socketio.transport.PollingTransport;

import java.util.Collections;
import java.util.List;

public class PacketListener {

    private final NamespacesHub namespacesHub;
    private final AckManager ackManager;
    private final CancelableScheduler scheduler;

    public PacketListener(AckManager ackManager, NamespacesHub namespacesHub, PollingTransport xhrPollingTransport,
            CancelableScheduler scheduler) {
        this.ackManager = ackManager;
        this.namespacesHub = namespacesHub;
        this.scheduler = scheduler;
    }

    public void onPacket(Packet packet, NamespaceClient client, Transport transport) {
        final AckRequest ackRequest = new AckRequest(packet, client);

        if (packet.isAckRequested()) {
            ackManager.initAckIndex(client.getSessionId(), packet.getAckId());
        }

        switch (packet.getType()) {
        case PING: {
            Packet outPacket = new Packet(PacketType.PONG);
            outPacket.setData(packet.getData());
            // TODO use future
            client.getBaseClient().send(outPacket, transport);

            if ("probe".equals(packet.getData())) {
                client.getBaseClient().send(new Packet(PacketType.NOOP), Transport.POLLING);
            } else {
                client.getBaseClient().schedulePingTimeout();
            }
            Namespace namespace = namespacesHub.get(packet.getNsp());
            namespace.onPing(client);
            break;
        }

        case UPGRADE: {
            client.getBaseClient().schedulePingTimeout();

            SchedulerKey key = new SchedulerKey(SchedulerKey.Type.UPGRADE_TIMEOUT, client.getSessionId());
            scheduler.cancel(key);

            client.getBaseClient().upgradeCurrentTransport(transport);
            break;
        }

        case MESSAGE: {
            client.getBaseClient().schedulePingTimeout();

            if (packet.getSubType() == PacketType.DISCONNECT) {
                client.onDisconnect();
            }

            if (packet.getSubType() == PacketType.CONNECT) {
                Namespace namespace = namespacesHub.get(packet.getNsp());
                namespace.onConnect(client);
                // send connect handshake packet back to client
                client.getBaseClient().send(packet, transport);
            }

            if (packet.getSubType() == PacketType.ACK
                    || packet.getSubType() == PacketType.BINARY_ACK) {
                ackManager.onAck(client, packet);
            }

            if (packet.getSubType() == PacketType.EVENT
                    || packet.getSubType() == PacketType.BINARY_EVENT) {
                Namespace namespace = namespacesHub.get(packet.getNsp());
                List<Object> args = Collections.emptyList();
                if (packet.getData() != null) {
                    args = packet.getData();
                }
                namespace.onEvent(client, packet.getName(), args, ackRequest);
            }
            break;
        }

        case CLOSE:
            client.getBaseClient().onChannelDisconnect();
            break;

        default:
            break;
        }
    }

}
