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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * 扫描器引擎
 */
public class ScannerEngine {

    private static final Logger log = LoggerFactory.getLogger(ScannerEngine.class);

    private static final List<? extends AnnotationScanner> ANNOTATIONS =
                    Arrays.asList(new OnConnectScanner(), new OnDisconnectScanner(), new OnEventScanner());

    private Method findSimilarMethod(Class<?> objectClazz, Method method) {
        Method[] methods = objectClazz.getDeclaredMethods();
        for (Method m : methods) {
            if (isEquals(m, method)) {
                return m;
            }
        }
        return null;
    }

    /**
     * 扫描事件监听器 并且添加到对应监听器队列中
     * @param namespace 名称空间
     * @param object 监听器
     * @param clazz 监听器的class
     * @throws IllegalArgumentException 异常抛出
     */
    public void scan(Namespace namespace, Object object, Class<?> clazz)
            throws IllegalArgumentException {
        Method[] methods = clazz.getDeclaredMethods();
        //确定一个类(B)是不是继承来自于另一个父类(A)，一个接口(A)是不是实现了另外一个接口(B)，或者两个类相同
        if (!clazz.isAssignableFrom(object.getClass())) {
            //如果不是类或者接口 说明是注解 AnnotationScanner
            for (Method method : methods) {
                for (AnnotationScanner annotationScanner : ANNOTATIONS) {
                    Annotation ann = method.getAnnotation(annotationScanner.getScanAnnotation());
                    if (ann != null) {
                        //校验注解
                        annotationScanner.validate(method, clazz);
                        //遍历匹配方法
                        Method m = findSimilarMethod(object.getClass(), method);
                        if (m != null) {
                            //添加到队列
                            annotationScanner.addListener(namespace, object, m, ann);
                        } else {
                            log.warn("Method similar to " + method.getName() + " can't be found in " + object.getClass());
                        }
                    }
                }
            }
        } else {
            for (Method method : methods) {
                //尝试遍历注解. 是否可以拿到注解
                for (AnnotationScanner annotationScanner : ANNOTATIONS) {
                    Annotation ann = method.getAnnotation(annotationScanner.getScanAnnotation());
                    if (ann != null) {
                        //校验注解
                        annotationScanner.validate(method, clazz);
                        //讲方法设置为可以访问
                        makeAccessible(method);
                        //添加到队列
                        annotationScanner.addListener(namespace, object, method, ann);
                    }
                }
            }

            if (clazz.getSuperclass() != null) {
                //递归
                scan(namespace, object, clazz.getSuperclass());
            } else if (clazz.isInterface()) {
                for (Class<?> superIfc : clazz.getInterfaces()) {
                    //递归
                    scan(namespace, object, superIfc);
                }
            }
        }

    }

    private boolean isEquals(Method method1, Method method2) {
        if (!method1.getName().equals(method2.getName())
                || !method1.getReturnType().equals(method2.getReturnType())) {
            return false;
        }

        return Arrays.equals(method1.getParameterTypes(), method2.getParameterTypes());
    }

    private void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

}
