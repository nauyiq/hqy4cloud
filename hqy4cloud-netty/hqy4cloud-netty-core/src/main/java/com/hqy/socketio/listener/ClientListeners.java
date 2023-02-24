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
package com.hqy.socketio.listener;

/**
 * 客户端监听器
 */
public interface ClientListeners {

    /**
     * 添加复合类型的事件监听器
     * @param eventName 事件名
     * @param listener 复合类型的事件监听器
     * @param eventClass 事件的class
     */
    void addMultiTypeEventListener(String eventName, MultiTypeEventListener listener, Class<?> ... eventClass);

    /**
     * 添加事件监听器
     * @param eventName 事件名
     * @param eventClass 事件的class
     * @param listener 监听器
     */
    <T> void addEventListener(String eventName, Class<T> eventClass, DataListener<T> listener);

    /**
     * 添加事件拦截器
     * @param eventInterceptor 事件拦截器
     */
    void addEventInterceptor(EventInterceptor eventInterceptor);

    /**
     * 添加断开连接事件监听器
     * @param listener 断开连接事件监听器
     */
    void addDisconnectListener(DisconnectListener listener);

    /**
     * 添加连接事件监听器
     * @param listener 连接事件监听器
     */
    void addConnectListener(ConnectListener listener);

    /**
     * 添加ping事件监听器
     * @param listener ping事件监听器
     */
    void addPingListener(PingListener listener);

    /**
     * 添加监听器 并且直接执行ScannerEngine.scan
     * @param listeners 监听器
     */
    void addListeners(Object listeners);

    /**
     * 添加监听器 并且直接执行ScannerEngine.scan
     * @param listeners 监听器
     * @param listenersClass 监听器class
     */
    void addListeners(Object listeners, Class<?> listenersClass);

    /**
     * 根据事件名移除所有的事件监听器
     * @param eventName 事件名
     */
    void removeAllListeners(String eventName);
    
}
