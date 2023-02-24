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
package com.hqy.socketio;


import com.hqy.socketio.protocol.Packet;

import java.util.Collection;

/**
 * 广播操作类
 * broadcast interface
 */
public interface BroadcastOperations extends ClientOperations {

    /**
     * 获取socket.io客户端集合
     * @return Collection<SocketIOClient>
     */
    Collection<SocketIOClient> getClients();

    /**
     * 往房间里的客户端广播消息
     * @param packet 数据包
     * @param ackCallback 回调
     */
    <T> void send(Packet packet, BroadcastAckCallback<T> ackCallback);

    /**
     * 广播事件, 排除某个客户端
     * @param name 事件名
     * @param excludedClient 被排除的客户端
     * @param data 数据
     */
    void sendEvent(String name, SocketIOClient excludedClient, Object... data);

    /**
     * 广播事件
     * @param name 事件名
     * @param data 数据
     * @param ackCallback 回调
     */
    <T> void sendEvent(String name, Object data, BroadcastAckCallback<T> ackCallback);

    /**
     * 广播事件, 排除某个客户端
     * @param name 事件名
     * @param data 数据
     * @param excludedClient 被排除的客户端
     * @param ackCallback 回调
     */
    <T> void sendEvent(String name, Object data, SocketIOClient excludedClient, BroadcastAckCallback<T> ackCallback);

}
