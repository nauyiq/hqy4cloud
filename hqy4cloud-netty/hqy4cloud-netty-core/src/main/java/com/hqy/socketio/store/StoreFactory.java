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
package com.hqy.socketio.store;


import com.hqy.socketio.Disconnectable;
import com.hqy.socketio.handler.AuthorizeHandler;
import com.hqy.socketio.namespace.NamespacesHub;
import com.hqy.socketio.protocol.JsonSupport;
import com.hqy.socketio.store.pubsub.PubSubStore;

import java.util.Map;
import java.util.UUID;

/**
 *
 * Creates a client Store and PubSubStore
 *
 */
public interface StoreFactory extends Disconnectable {

    PubSubStore pubSubStore();

    <K, V> Map<K, V> createMap(String name);

    Store createStore(UUID sessionId);

    void init(NamespacesHub namespacesHub, AuthorizeHandler authorizeHandler, JsonSupport jsonSupport);

    void shutdown();

}
