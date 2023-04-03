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
package com.corundumstudio.socketio;


import com.corundumstudio.socketio.listener.ClientListeners;

import java.util.Collection;
import java.util.UUID;

/**
 * socketio中的namespace的概念。如果希望服务端发送的信息在所有客户端都能收到，那么使用默认的namespace / 就好了。
 * 但是如果想把发送信息的服务器作为第三方应用给不同客户端使用，就需要为每一个客户端定义一个namespace
 * Fully thread-safe.
 */
public interface SocketIONamespace extends ClientListeners {

    /**
     * 获取名称空间name
     * @return String
     */
    String getName();

    BroadcastOperations getBroadcastOperations();

    BroadcastOperations getRoomOperations(String room);

    /**
     * Get all clients connected to namespace
     *
     * @return collection of clients
     */
    Collection<SocketIOClient> getAllClients();

    /**
     * Get client by uuid connected to namespace
     *
     * @param uuid - id of client
     * @return client
     */
    SocketIOClient getClient(UUID uuid);

}
