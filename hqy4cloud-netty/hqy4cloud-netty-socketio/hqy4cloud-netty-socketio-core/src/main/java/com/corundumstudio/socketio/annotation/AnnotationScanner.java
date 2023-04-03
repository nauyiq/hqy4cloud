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
package com.corundumstudio.socketio.annotation;


import com.corundumstudio.socketio.namespace.Namespace;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 注解扫描器
 * 三个注解 @OnConnect, @OnDisconnect, @OnEvent
 */
public interface AnnotationScanner {

    /**
     * 获取注解扫描器
     * @return Class<? extends Annotation>
     */
    Class<? extends Annotation> getScanAnnotation();

    /**
     * 添加到Namespace 队列监听器
     * @param namespace 名称空间
     * @param object 数据
     * @param method 反射方法
     * @param annotation 注解
     */
    void addListener(Namespace namespace, Object object, Method method, Annotation annotation);

    /**
     * 注解校验 需要参数里面存在SocketIOClient
     * @param method 反射方法
     * @param clazz class
     */
    void validate(Method method, Class<?> clazz);

}