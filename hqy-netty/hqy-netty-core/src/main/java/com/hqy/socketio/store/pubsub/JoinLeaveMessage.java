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
package com.hqy.socketio.store.pubsub;

import java.util.UUID;

public class JoinLeaveMessage extends PubSubMessage {

    private static final long serialVersionUID = -944515928988033174L;

    private UUID sessionId;
    private String namespace;
    private String room;

    public JoinLeaveMessage() {
    }

    public JoinLeaveMessage(UUID id, String room, String namespace) {
        super();
        this.sessionId = id;
        this.room = room;
        this.namespace = namespace;
    }

    public String getNamespace() {
        return namespace;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public String getRoom() {
        return room;
    }

}
